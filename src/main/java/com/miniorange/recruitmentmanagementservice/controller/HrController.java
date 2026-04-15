package com.miniorange.recruitmentmanagementservice.controller;

import com.miniorange.recruitmentmanagementservice.dto.request.PostJobRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.JobApplicationResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.JobResponse;
import com.miniorange.recruitmentmanagementservice.repository.JobApplicationRepository;
import com.miniorange.recruitmentmanagementservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class HrController {

    private final JobService jobService;
    private final JobApplicationRepository jobApplicationRepository;

    /**
     * HR posts a new job with skillset and description (technical or non-technical)
     */
    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> postJob(@Valid @RequestBody PostJobRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        JobResponse response = jobService.postJob(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * HR views jobs they have posted
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
        List<JobResponse> jobs = jobService.getJobsByHr(userDetails.getUsername());
        return ResponseEntity.ok(jobs);
    }

    /**
     * HR views applications for a specific job
     */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<JobApplicationResponse>> getJobApplications(@PathVariable UUID jobId) {
        List<JobApplicationResponse> applications = jobApplicationRepository.findByJobId(jobId).stream()
                .map(app -> JobApplicationResponse.builder()
                        .id(app.getId())
                        .jobId(app.getJob().getId())
                        .jobTitle(app.getJob().getTitle())
                        .companyName(app.getJob().getCompany().getName())
                        .status(app.getStatus().name())
                        .candidateName(app.getCandidateProfile().getUser().getFullName())
                        .candidateEmail(app.getCandidateProfile().getUser().getEmail())
                        .appliedAt(app.getAppliedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(applications);
    }
}

