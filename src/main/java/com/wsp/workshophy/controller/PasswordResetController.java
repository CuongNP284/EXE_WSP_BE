package com.wsp.workshophy.controller;

import com.wsp.workshophy.dto.request.ApiResponse;
import com.wsp.workshophy.dto.request.ResetPasswordRequest;
import com.wsp.workshophy.dto.request.VerifyOtpRequest;
import com.wsp.workshophy.service.PasswordResetService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetController {
    PasswordResetService passwordResetService;

    // Endpoint để yêu cầu reset mật khẩu
    @PostMapping("/request-reset-password")
    public ApiResponse<String> requestResetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ApiResponse.<String>builder()
                .result("OTP has been sent to your email")
                .message("Password reset request successful")
                .build();
    }

    // Endpoint để xác nhận OTP và đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody VerifyOtpRequest request) {
        passwordResetService.resetPassword(request);
        return ApiResponse.<String>builder()
                .result("Password has been reset successfully")
                .message("Password reset successful")
                .build();
    }
}
