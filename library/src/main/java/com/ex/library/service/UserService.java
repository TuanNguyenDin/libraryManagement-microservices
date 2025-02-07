package com.ex.library.service;

import com.ex.library.dto.request.EmailInforRequest;
import com.ex.library.dto.request.SendEmailRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.request.UserCreateRequest;
import com.ex.library.dto.response.UserResponse;
import com.ex.library.entity.Users;
import com.ex.library.entity.VerifyCodeChangeEmail;
import com.ex.library.exception.CustomException;
import com.ex.library.exception.ErrorCode;
import com.ex.library.mapper.UserMapper;
import com.ex.library.repository.UserRepository;
import com.ex.library.repository.VerifyCodeChangeEmailRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    VerifyCodeChangeEmailRepository verifyCodeRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    public static boolean isStrongPassword(String password) {
        return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    //CRUD user
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<UserResponse> createUser(UserCreateRequest request) throws MessagingException {

        APIResponse<UserResponse> usersAPIResponse = new APIResponse<>();

        if (userRepository.findOneByName(request.getName()) != null) {
            throw new CustomException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.findOneByEmail(request.getEmail()) != null) {
            throw new CustomException(ErrorCode.EMAIL_EXISTED);
        }
        if (!isStrongPassword(request.getPassword())) {
            throw new CustomException(ErrorCode.WEAK_PASSWORD);
        }

        String password = request.getPassword();
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        request.setPassword(hashPassword);

        Date now = new Date();
        request.setCreatedAt(now);
        Users user;
        user = userMapper.toUser(request);
        Users save = userRepository.save(user);

        String url = "localhost/user/verify/";
        SendEmailRequest mailComform = SendEmailRequest.builder()
                .to(EmailInforRequest.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .subject("Demo mail")
                .build();
        usersAPIResponse.setResponse(userMapper.toUserResponse(user));
        emailService.sendEmail(mailComform, "welcomeEmail", "", url + usersAPIResponse.getResponse().getId());

        return usersAPIResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> findUsersBanned(boolean isBanned) {
        return userRepository.findAllByIsBanned(isBanned);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Users> findUsersActive(boolean isActive) {
        return userRepository.findAllByIsActive(isActive);
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.name == authentication.name")
    public UserResponse getUser(String id) {
        Users users = userRepository.findById(id).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(users);
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.name == authentication.name")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<Users> updateUser(String id, UserCreateRequest request) {
        Users user = checkUserExist(id, request.getEmail(), request.getName());
        APIResponse<Users> usersAPIResponse = new APIResponse<>();

        if (user.isActive()) {

            String password = request.getPassword();
            if (BCrypt.checkpw(password, user.getPassword())) {
                Date updateDate = new Date();
                request.setUpdateAt(updateDate);
                userMapper.updateUser(user, request);
                usersAPIResponse.setResponse(userRepository.save(user));
            } else {
                throw new CustomException(ErrorCode.WRONG_PASSWORD);
            }
        } else {
            usersAPIResponse.setMessage("User is inactive, Please active user first!");
        }
        return usersAPIResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<Users> deleteUser(String id) {
        Users users = userRepository.findById(id).orElseThrow(()
                -> new CustomException(ErrorCode.USER_NOT_EXISTED));
        APIResponse<Users> response = new APIResponse<>();
        if (users.isActive()) {
            userRepository.deleteById(id);
            response.setMessage("User has been delete");
        }
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<List<Users>> findUserFollowDate(Date beginDate, Date endDate) {
        List<Users> allUser = userRepository.findAll();
        APIResponse<List<Users>> response = new APIResponse<>();
        response.setResponse(
                allUser.stream().filter(user ->
                                user.getCreatedAt().after(beginDate)
                                        && user.getCreatedAt().before(endDate))
                        .collect(Collectors.toList()));
        return response;
    }


    @PostAuthorize("returnObject.response.name == authentication.name")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<UserResponse> changeUserEmail(String userId, String newEmail) throws MessagingException {
        Users user = checkUserExist(userId, "", "");

        VerifyCodeChangeEmail code = generateVerifyCode(user);
        user.setEmail(newEmail);
        verifyCodeRepository.save(code);
        //need handle spawn request !!!

        SendEmailRequest mailComform = SendEmailRequest.builder()
                .to(EmailInforRequest.builder()
                        .name(user.getName())
                        .email(newEmail)
                        .build())
                .subject("Email verify change email")
                .build();
        emailService.sendEmail(mailComform, "changeEmail", code.getCode(), "");

        APIResponse<UserResponse> response = new APIResponse<>();
        response.setResponse(userMapper.toUserResponse(user));
        response.setMessage("User change email request successfully. A mail verify has been sent to " + newEmail + ".");

        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    public APIResponse<Void> confirmChangeEmail(String userId, String verifyCode) {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_EXISTED));

        VerifyCodeChangeEmail code = verifyCodeRepository.findOneByCode(verifyCode).orElseThrow(() ->
                new CustomException(ErrorCode.CODE_NOT_FOUND));

        if (code.getExpiryDate().before(new Date())) {
            user.setEmail(code.getNewEmail());
            user.setUpdateAt(new java.sql.Date(System.currentTimeMillis()));
            userRepository.save(user);
            return APIResponse.<Void>builder()
                    .message("User email update successfully")
                    .build();
        } else {
            return APIResponse.<Void>builder()
                    .message("Verify code expired, please try again later.")
                    .build();
        }
    }


    @PostAuthorize("hasRole('ADMIN') || returnObject.response.name == authentication.name")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<UserResponse> resetPassword(String email) throws MessagingException {
        if (!isValidEmail(email)) {
            throw new CustomException(ErrorCode.INVALID_VALUE_INPUT);
        }
        Users user = checkUserExist("", "", email);

        // Generate a new random password
        String newPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setActive(false);
        user.setUpdateAt(new java.sql.Date(System.currentTimeMillis()));
        userRepository.save(user);

        // Send an email to the user
        SendEmailRequest mailComform = SendEmailRequest.builder()
                .to(EmailInforRequest.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .subject("Your Password Has Been Reset")
                .build();
        emailService.sendEmail(mailComform, "resetPassword", "", "");

        APIResponse<UserResponse> response = new APIResponse<>();
        response.setResponse(userMapper.toUserResponse(user));
        response.setMessage("Password reset successfully for user email: " + email);

        log.info("Password reset successfully for user email: {}", email);
        return response;
    }


    @PostAuthorize("hasRole('ADMIN') || returnObject.response.name == authentication.name")
    @Transactional(rollbackOn = Exception.class)
    public APIResponse<UserResponse> updatePassword(String userId, String currentPassword, String newPassword)
            throws MessagingException {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isActive()) {
            throw new CustomException(ErrorCode.USER_INACTIVE);
        }

        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
        if (!isStrongPassword(newPassword)) {
            throw new CustomException(ErrorCode.WEAK_PASSWORD);
        }

        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedNewPassword);
        user.setActive(false);
        user.setUpdateAt(new java.sql.Date(System.currentTimeMillis()));

        // userRepository.save(user);

        // Send confirmation email
        SendEmailRequest mailConfirm = SendEmailRequest.builder()
                .to(EmailInforRequest.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .subject("Your Password Has Been Updated")
                .build();
        emailService.sendEmail(mailConfirm, "updatePasswordConfirmation", "", "");

        APIResponse<UserResponse> response = new APIResponse<>();
        response.setResponse(userMapper.toUserResponse(user));
        response.setMessage("Password has been changed for userId: " + userId);

        log.info("Password updated successfully for userId: {}", userId);
        return response;
    }

    @Transactional(rollbackOn = Exception.class)
    public APIResponse<UserResponse> activeUser(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() ->
                new CustomException(ErrorCode.USER_NOT_EXISTED));

        String message;
        if (!user.isActive()) {
            user.setActive(true);
            user.setUpdateAt(new java.sql.Date(System.currentTimeMillis()));
            userRepository.save(user);
            message = "User active successfully for userId: " + userId;
        } else {
            message = "your account has been activated";
        }
        return APIResponse.<UserResponse>builder()
                .response(userMapper.toUserResponse(user))
                .message(message)
                .build();
    }


    private Users checkUserExist(String userId, String name, String Email) {
        Users user = null;
        if (!userId.isEmpty()) {
            user = userRepository.findById(userId).orElseThrow(() ->
                    new CustomException(ErrorCode.USER_NOT_EXISTED));
            return user;
        } else if (!name.isEmpty()) {
            user = userRepository.findOneByName(name);
            if (user == null) {
                throw new CustomException(ErrorCode.USER_NOT_EXISTED);
            }
        } else if (!Email.isEmpty()) {
            user = userRepository.findOneByEmail(Email);
            if (user == null) {
                throw new CustomException(ErrorCode.USER_NOT_EXISTED);
            }
        }
        return user;
    }

    private VerifyCodeChangeEmail generateVerifyCode(Users user) {
        String verifyCode = java.util.UUID.randomUUID().toString().substring(0, 6);
        VerifyCodeChangeEmail code = VerifyCodeChangeEmail.builder()
                .code(verifyCode)
                .users(user)
                .expiryDate(new Date(System.currentTimeMillis() + 3600000))
                .oldEmail(user.getEmail())
                .build();
        return code;
    }
}
