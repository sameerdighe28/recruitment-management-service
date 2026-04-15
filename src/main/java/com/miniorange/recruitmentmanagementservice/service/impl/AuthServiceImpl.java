package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.dto.request.LoginRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.OtpVerificationRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.AuthResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.OtpSentResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;
import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.Role;
import com.miniorange.recruitmentmanagementservice.exception.BadRequestException;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import com.miniorange.recruitmentmanagementservice.security.CustomUserDetailsService;
import com.miniorange.recruitmentmanagementservice.security.JwtUtils;
import com.miniorange.recruitmentmanagementservice.service.AuthService;
import com.miniorange.recruitmentmanagementservice.service.OtpService;
import com.miniorange.recruitmentmanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final OtpService otpService;
    private final UserService userService;

    @Override
    public OtpSentResponse login(LoginRequest request) {
        // Step 1: Authenticate with email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Step 2: Get the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Step 3: Generate and send OTP
        otpService.createAndSendOtp(user);

        // Step 4: Mask mobile number for response
        String maskedMobile = maskMobile(user.getMobileNumber());

        return OtpSentResponse.builder()
                .message("OTP has been sent to your email and mobile number")
                .email(user.getEmail())
                .maskedMobile(maskedMobile)
                .build();
    }

    @Override
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        // Validate OTP
        otpService.validateOtp(request.getEmail(), request.getOtp());

        // Get user and generate JWT
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails, user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .role(user.getRole().name())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserResponse registerCandidate(RegisterUserRequest request) {
        request.setRole(Role.CANDIDATE);
        return userService.createUser(request);
    }

    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return "****";
        }
        return "****" + mobile.substring(mobile.length() - 4);
    }
}

