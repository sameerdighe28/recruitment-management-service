package com.miniorange.recruitmentmanagementservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String address;

    private String website;
}

