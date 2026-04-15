package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.dto.request.CandidateProfileRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CandidateProfileResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.JobApplicationResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.JobResponse;

import java.util.List;
import java.util.UUID;

public interface CandidateService {

    CandidateProfileResponse createProfile(CandidateProfileRequest request, String candidateEmail);

    CandidateProfileResponse updateProfile(CandidateProfileRequest request, String candidateEmail);

    CandidateProfileResponse getProfile(String candidateEmail);

    List<JobResponse> getAvailableJobs(String candidateEmail);

    JobApplicationResponse applyToJob(UUID jobId, String candidateEmail);

    List<JobApplicationResponse> getMyApplications(String candidateEmail);
}

