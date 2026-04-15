package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.dto.request.PostJobRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.JobResponse;
import com.miniorange.recruitmentmanagementservice.entity.Job;
import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.JobCategory;
import com.miniorange.recruitmentmanagementservice.exception.BadRequestException;
import com.miniorange.recruitmentmanagementservice.exception.ResourceNotFoundException;
import com.miniorange.recruitmentmanagementservice.repository.JobRepository;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import com.miniorange.recruitmentmanagementservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public JobResponse postJob(PostJobRequest request, String hrEmail) {
        User hr = userRepository.findByEmail(hrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("HR user not found"));

        if (hr.getCompany() == null) {
            throw new BadRequestException("HR must be associated with a company to post jobs");
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .skillset(request.getSkillset())
                .category(request.getCategory())
                .location(request.getLocation())
                .company(hr.getCompany())
                .postedBy(hr)
                .build();

        job = jobRepository.save(job);
        return mapToJobResponse(job);
    }

    @Override
    public List<JobResponse> getJobsByHr(String hrEmail) {
        User hr = userRepository.findByEmail(hrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("HR user not found"));
        return jobRepository.findByPostedById(hr.getId()).stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> getJobsByCategory(String category) {
        JobCategory jobCategory = JobCategory.valueOf(category.toUpperCase());
        return jobRepository.findByCategory(jobCategory).stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse getJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return mapToJobResponse(job);
    }

    private JobResponse mapToJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .skillset(job.getSkillset())
                .category(job.getCategory().name())
                .location(job.getLocation())
                .companyName(job.getCompany().getName())
                .postedBy(job.getPostedBy().getFullName())
                .build();
    }
}

