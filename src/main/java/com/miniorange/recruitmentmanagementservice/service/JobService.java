package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.dto.request.PostJobRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.JobResponse;

import java.util.List;
import java.util.UUID;

public interface JobService {

    JobResponse postJob(PostJobRequest request, String hrEmail);

    List<JobResponse> getJobsByHr(String hrEmail);

    List<JobResponse> getAllJobs();

    List<JobResponse> getJobsByCategory(String category);

    JobResponse getJob(UUID jobId);
}

