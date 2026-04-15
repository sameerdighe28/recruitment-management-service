package com.miniorange.recruitmentmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {

    private UUID id;

    private UUID jobId;

    private String jobTitle;

    private String companyName;

    private String status;

    private String candidateName;

    private String candidateEmail;

    private LocalDateTime appliedAt;
}

