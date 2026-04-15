package com.miniorange.recruitmentmanagementservice.controller;

import com.miniorange.recruitmentmanagementservice.dto.request.LoginRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.OtpVerificationRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.AuthResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.OtpSentResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;
import com.miniorange.recruitmentmanagementservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Step 1: Login with email and password.
     * On success, an OTP is sent to the user's email and mobile number.
     */
    @PostMapping("/login")
    public ResponseEntity<OtpSentResponse> login(@Valid @RequestBody LoginRequest request) {
        OtpSentResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2: Verify OTP received on email/mobile.
     * On success, returns a JWT token for subsequent API calls.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Public endpoint for candidate self-registration.
     */
    @PostMapping("/register/candidate")
    public ResponseEntity<UserResponse> registerCandidate(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = authService.registerCandidate(request);
        return ResponseEntity.ok(response);
    }
}

