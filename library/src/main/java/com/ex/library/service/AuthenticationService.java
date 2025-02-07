package com.ex.library.service;

import com.ex.library.dto.request.AuthenticationRequest;
import com.ex.library.dto.request.IntrospectRequest;
import com.ex.library.dto.request.LogoutRequest;
import com.ex.library.dto.response.AuthenticationResponse;
import com.ex.library.dto.response.IntrospectResponse;
import com.ex.library.entity.InvalidateToken;
import com.ex.library.entity.Users;
import com.ex.library.exception.CustomException;
import com.ex.library.exception.ErrorCode;
import com.ex.library.repository.InvalidateTokenRepository;
import com.ex.library.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Service
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    UserRepository repository;
    InvalidateTokenRepository invalidateTokenRepository;

    @NonFinal
    @Value("${spring.jwt.signerKey}")
    private String SECRET_KEY;

    @NonFinal
    @Value("${spring.jwt.valid-duration}")
    private long VALID_DURATION;

    @NonFinal
    @Value("${spring.jwt.refreshable-duration}")
    private long REFRESH_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean validToken = true;

        try {
            verifyToken(token);
        } catch (CustomException e) {
            e.printStackTrace();
            validToken = false;
        }

        return IntrospectResponse.builder()
                .valid(validToken)
                .build();
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        Users user = repository.findOneByName(request.getName());
        if (user != null) {
            boolean checkUser = BCrypt.checkpw(request.getPassword(), user.getPassword());
            if (!checkUser) {
                throw new CustomException(ErrorCode.UNAUTHENTICATED);
            } else {
                if (user.isActive() || user.isAdmin()) {
                    var token = generateToken(request.getName());
                    return AuthenticationResponse.builder()
                            .JWTToken(token)
                            .authentication(true)
                            .build();
                } else throw new CustomException(ErrorCode.USER_INACTIVE);
            }
        } else {
            throw new CustomException(ErrorCode.USER_NOT_EXISTED);
        }

    }

    public AuthenticationResponse refreshToken(LogoutRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        var signToken = verifyToken(token);

        var jwtId = signToken.getJWTClaimsSet().getJWTID();
        var jwtExpiryDate = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jwtId)
                .expiryDate(new java.sql.Date(jwtExpiryDate.getTime()))
                .build();
        invalidateTokenRepository.save(invalidateToken);

        String username = signToken.getJWTClaimsSet().getSubject();
        var user = repository.findOneByName(username);

        return AuthenticationResponse.builder()
                .JWTToken(generateToken(user))
                .authentication(true)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String tokenId = signToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();
        java.sql.Date sqlExpiration = new java.sql.Date(expirationTime.getTime());

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(tokenId)
                .expiryDate(sqlExpiration)
                .build();
        invalidateTokenRepository.save(invalidateToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier jwsVerifier = new MACVerifier(SECRET_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verify = signedJWT.verify(jwsVerifier);

        log.info(expirationTime.toString());

        if (!verify || expirationTime.before(new Date())) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(String name) {
        Users users = repository.findOneByName(name);
        return generateToken(users);
    }

    private String generateToken(Users users) {

        String role;
        if (users.isAdmin()) {
            role = "admin";
        } else {
            role = "user";
        }

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(users.getName())
                .issuer("Library")
                .expirationTime(new Date(System.currentTimeMillis() + VALID_DURATION))
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", role.toUpperCase())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY));
            return jwsObject.serialize();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }
    }
}
