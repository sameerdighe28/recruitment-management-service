package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Recruitment Management - OTP Verification");
            message.setText("Your OTP for login verification is: " + otp
                    + "\n\nThis OTP is valid for 5 minutes."
                    + "\n\nIf you did not request this, please ignore this email.");
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}. Error: {}", toEmail, e.getMessage());
            // Log the OTP to console as fallback for development
            log.info("FALLBACK - OTP for {}: {}", toEmail, otp);
        }
    }
}

