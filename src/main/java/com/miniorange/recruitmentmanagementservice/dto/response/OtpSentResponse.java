package com.miniorange.recruitmentmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpSentResponse {

    private String message;

    private String email;

    private String maskedMobile;
}

