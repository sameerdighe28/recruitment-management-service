package com.miniorange.recruitmentmanagementservice.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
}

