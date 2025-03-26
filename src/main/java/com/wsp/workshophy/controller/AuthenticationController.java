package com.wsp.workshophy.controller;

import java.text.ParseException;

import com.wsp.workshophy.dto.request.*;
import com.wsp.workshophy.dto.request.User.UserCreationRequest;
import com.wsp.workshophy.dto.response.UserResponse;
import com.wsp.workshophy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.wsp.workshophy.dto.response.AuthenticationResponse;
import com.wsp.workshophy.dto.response.IntrospectResponse;
import com.wsp.workshophy.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/registerCustomer")
    ApiResponse<UserResponse> registerCustomer(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.registerCustomer(request))
                .message("Customer registered successfully. Please check your email to verify your account.")
                .build();
    }

    @PostMapping("/registerOrganizer")
    ApiResponse<UserResponse> registerOrganizer(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.registerOrganizer(request))
                .message("Organizer registered successfully. Please check your email to verify your account.")
                .build();
    }



    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Authentication!")
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/customer/verify-email")
    ApiResponse<String> verifyEmailByCustomer(@RequestParam("token") String token) {
        userService.verifyEmailForCustomer(token);
        return ApiResponse.<String>builder()
                .message("Email verified successfully! You can now log in to your account as a customer.")
                .build();
    }

    @GetMapping("/organizer/verify-email")
    ApiResponse<String> verifyEmailByOrganizer(@RequestParam("token") String token) {
        userService.verifyEmailForOrganizer(token);
        return ApiResponse.<String>builder()
                .message("Email verified successfully! You can now log in to your account as an organizer.")
                .build();
    }

    // Endpoint để gửi lại email xác thực
    @PostMapping("/resend-verification")
    ApiResponse<String> resendVerification(@RequestBody ResendVerificationRequest request) {
        userService.resendVerificationEmail(request.getEmail());
        return ApiResponse.<String>builder()
                .message("Verification email sent successfully. Please check your email.")
                .build();
    }
}
