package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.service.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Override
    public void sendOtpSms(String mobileNumber, String otp) {
        if (!twilioEnabled) {
            log.info("Twilio is disabled. SMS OTP for {}: {}", mobileNumber, otp);
            return;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(mobileNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "Your Recruitment Management OTP is: " + otp + ". Valid for 5 minutes."
            ).create();
            log.info("OTP SMS sent successfully to: {}. SID: {}", mobileNumber, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send OTP SMS to: {}. Error: {}", mobileNumber, e.getMessage());
            log.info("FALLBACK - SMS OTP for {}: {}", mobileNumber, otp);
        }
    }
}

