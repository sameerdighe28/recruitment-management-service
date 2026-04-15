package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.entity.OtpToken;
import com.miniorange.recruitmentmanagementservice.entity.User;

public interface OtpService {

    String generateOtp();

    OtpToken createAndSendOtp(User user);

    boolean validateOtp(String email, String otp);
}

