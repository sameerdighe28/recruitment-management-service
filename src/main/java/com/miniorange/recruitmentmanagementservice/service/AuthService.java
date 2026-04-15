package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.dto.request.LoginRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.OtpVerificationRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.AuthResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.OtpSentResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;

public interface AuthService {

    OtpSentResponse login(LoginRequest request);

    AuthResponse verifyOtp(OtpVerificationRequest request);

    UserResponse registerCandidate(RegisterUserRequest request);
}

