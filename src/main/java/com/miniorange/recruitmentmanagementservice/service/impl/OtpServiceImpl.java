package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.entity.OtpToken;
import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.exception.OtpExpiredException;
import com.miniorange.recruitmentmanagementservice.repository.OtpTokenRepository;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import com.miniorange.recruitmentmanagementservice.service.EmailService;
import com.miniorange.recruitmentmanagementservice.service.OtpService;
import com.miniorange.recruitmentmanagementservice.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${app.otp.expiration-minutes}")
    private int otpExpirationMinutes;

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public OtpToken createAndSendOtp(User user) {
        String otp = generateOtp();

        OtpToken otpToken = OtpToken.builder()
                .otp(otp)
                .email(user.getEmail())
                .mobile(user.getMobileNumber())
                .emailVerified(false)
                .mobileVerified(false)
                .used(false)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .user(user)
                .build();

        otpTokenRepository.save(otpToken);

        // Send OTP via email and SMS (same OTP for both)
        emailService.sendOtpEmail(user.getEmail(), otp);
        smsService.sendOtpSms(user.getMobileNumber(), otp);

        log.info("OTP created and sent for user: {}", user.getEmail());
        return otpToken;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        OtpToken otpToken = otpTokenRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new OtpExpiredException("No OTP found. Please request a new one."));

        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired. Please request a new one.");
        }

        if (!otpToken.getOtp().equals(otp)) {
            throw new OtpExpiredException("Invalid OTP. Please try again.");
        }

        // Mark OTP as used
        otpToken.setUsed(true);
        otpToken.setEmailVerified(true);
        otpToken.setMobileVerified(true);
        otpTokenRepository.save(otpToken);

        return true;
    }
}

