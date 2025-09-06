package com.kaiser.messenger_server.controllers;

import java.text.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kaiser.messenger_server.dto.request.AuthRequest;
import com.kaiser.messenger_server.dto.request.CreateUserRequest;
import com.kaiser.messenger_server.dto.request.ForgotPasswordRequest;
import com.kaiser.messenger_server.dto.request.LogoutRequest;
import com.kaiser.messenger_server.dto.request.VerifyUserRequest;
import com.kaiser.messenger_server.dto.response.ApiResponse;
import com.kaiser.messenger_server.dto.response.AuthResponse;
import com.kaiser.messenger_server.dto.response.UserResponse;
import com.kaiser.messenger_server.services.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        AuthResponse result = authService.login(request, response);

        return ApiResponse.<AuthResponse>builder()
            .message("User login")
            .result(result)
            .build();
    }

    @GetMapping("/account")
    ApiResponse<UserResponse> getAccount(){
        UserResponse result = authService.getAccount();

        return ApiResponse.<UserResponse>builder()
            .message("Get user account")
            .result(result)
            .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws JOSEException, ParseException {
        authService.logout(request);
        
        return ApiResponse.<Void>builder()
            .message("User logout")
            .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) throws JOSEException, ParseException {
        String token = authHeader.substring(7);
        AuthResponse result = authService.refreshToken(token, response);
        
        return ApiResponse.<AuthResponse>builder()
            .message("Refresh new token")
            .result(result)
            .build();
    }

    @PostMapping("/register")
    ApiResponse<UserResponse> register(@RequestBody @Valid CreateUserRequest request){
        UserResponse result = authService.register(request);

        return ApiResponse.<UserResponse>builder()
            .message("Register user")
            .result(result)
            .build();
    }

    @PostMapping("/verify")
    ApiResponse<UserResponse> verifyCode(@RequestBody @Valid VerifyUserRequest request){
        UserResponse result = authService.verifyUser(request);

        return ApiResponse.<UserResponse>builder()
            .message("Verify user")
            .result(result)
            .build();
    }

    @PostMapping("/resend")
    ApiResponse<Void> resendCode(@RequestBody @Valid VerifyUserRequest request){
        authService.resendCode(request);

        return ApiResponse.<Void>builder()
            .message("Resend verify code")
            .build();
    }

    @PostMapping("/forgot")
    ApiResponse<UserResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request){
        UserResponse result = authService.forgotPassword(request);

        return ApiResponse.<UserResponse>builder()
            .message("Reset user password")
            .result(result)
            .build();
    }
}
