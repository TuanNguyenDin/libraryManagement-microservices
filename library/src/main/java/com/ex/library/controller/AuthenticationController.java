package com.ex.library.controller;

import com.ex.library.dto.request.AuthenticationRequest;
import com.ex.library.dto.request.IntrospectRequest;
import com.ex.library.dto.request.LogoutRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.response.AuthenticationResponse;
import com.ex.library.dto.response.IntrospectResponse;
import com.ex.library.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService service;

    @PostMapping("/login")
    APIResponse<AuthenticationResponse> authenticationResponseAPIResponse(@RequestBody AuthenticationRequest request) {
        var result = service.authentication(request);
        return APIResponse.<AuthenticationResponse>builder()
                .response(result).build();
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> introspectResponseAPIResponse(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = service.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .response(result).build();
    }

    @PostMapping("/logout")
    APIResponse logoutResponseAPIResponse(@RequestBody LogoutRequest token) throws ParseException, JOSEException {
        service.logout(token);
        return APIResponse.builder().message("LOGOUT SUCCESS").build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> refreshTokenAPIResponse(@RequestBody LogoutRequest token) throws ParseException, JOSEException {
        var result = service.refreshToken(token);
        return APIResponse.<AuthenticationResponse>builder()
                .response(result).build();
    }
}
