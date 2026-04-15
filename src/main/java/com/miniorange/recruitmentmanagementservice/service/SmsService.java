package com.miniorange.recruitmentmanagementservice.service;

public interface SmsService {

    void sendOtpSms(String mobileNumber, String otp);
}

