package com.miniorange.recruitmentmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileResponse {

    private UUID id;

    private String category;

    private List<String> skills;

    private String resumeUrl;

    private int experienceYears;

    private String candidateName;

    private String candidateEmail;
}

