package com.miniorange.recruitmentmanagementservice.dto.request;

import com.miniorange.recruitmentmanagementservice.enums.CandidateCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CandidateProfileRequest {

    @NotNull(message = "Category is required (TECHNICAL or NON_TECHNICAL)")
    private CandidateCategory category;

    private List<String> skills;

    private String resumeUrl;

    private int experienceYears;
}

