package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.dto.request.CandidateProfileRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CandidateProfileResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.JobApplicationResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.JobResponse;
import com.miniorange.recruitmentmanagementservice.entity.CandidateProfile;
import com.miniorange.recruitmentmanagementservice.entity.Job;
import com.miniorange.recruitmentmanagementservice.entity.JobApplication;
import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.ApplicationStatus;
import com.miniorange.recruitmentmanagementservice.enums.CandidateCategory;
import com.miniorange.recruitmentmanagementservice.enums.JobCategory;
import com.miniorange.recruitmentmanagementservice.exception.BadRequestException;
import com.miniorange.recruitmentmanagementservice.exception.ResourceNotFoundException;
import com.miniorange.recruitmentmanagementservice.repository.CandidateProfileRepository;
import com.miniorange.recruitmentmanagementservice.repository.JobApplicationRepository;
import com.miniorange.recruitmentmanagementservice.repository.JobRepository;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import com.miniorange.recruitmentmanagementservice.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public CandidateProfileResponse createProfile(CandidateProfileRequest request, String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (candidateProfileRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Profile already exists. Use update endpoint.");
        }

        CandidateProfile profile = CandidateProfile.builder()
                .category(request.getCategory())
                .skills(request.getSkills())
                .resumeUrl(request.getResumeUrl())
                .experienceYears(request.getExperienceYears())
                .user(user)
                .build();

        profile = candidateProfileRepository.save(profile);
        return mapToProfileResponse(profile);
    }

    @Override
    public CandidateProfileResponse updateProfile(CandidateProfileRequest request, String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please create one first."));

        profile.setCategory(request.getCategory());
        profile.setSkills(request.getSkills());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setExperienceYears(request.getExperienceYears());

        profile = candidateProfileRepository.save(profile);
        return mapToProfileResponse(profile);
    }

    @Override
    public CandidateProfileResponse getProfile(String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found. Please create one first."));

        return mapToProfileResponse(profile);
    }

    @Override
    public List<JobResponse> getAvailableJobs(String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Please create your profile and choose a category (TECHNICAL/NON_TECHNICAL) first"));

        // Map candidate category to job category
        JobCategory jobCategory = profile.getCategory() == CandidateCategory.TECHNICAL
                ? JobCategory.TECHNICAL
                : JobCategory.NON_TECHNICAL;

        return jobRepository.findByCategory(jobCategory).stream()
                .map(this::mapToJobResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationResponse applyToJob(UUID jobId, String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Please create your profile first before applying to jobs"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Check if already applied
        if (jobApplicationRepository.existsByJobIdAndCandidateProfileId(jobId, profile.getId())) {
            throw new BadRequestException("You have already applied to this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidateProfile(profile)
                .status(ApplicationStatus.APPLIED)
                .build();

        application = jobApplicationRepository.save(application);
        return mapToApplicationResponse(application);
    }

    @Override
    public List<JobApplicationResponse> getMyApplications(String candidateEmail) {
        User user = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return jobApplicationRepository.findByCandidateProfileId(profile.getId()).stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    private CandidateProfileResponse mapToProfileResponse(CandidateProfile profile) {
        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .category(profile.getCategory().name())
                .skills(profile.getSkills())
                .resumeUrl(profile.getResumeUrl())
                .experienceYears(profile.getExperienceYears())
                .candidateName(profile.getUser().getFullName())
                .candidateEmail(profile.getUser().getEmail())
                .build();
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

    private JobApplicationResponse mapToApplicationResponse(JobApplication application) {
        return JobApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany().getName())
                .status(application.getStatus().name())
                .candidateName(application.getCandidateProfile().getUser().getFullName())
                .candidateEmail(application.getCandidateProfile().getUser().getEmail())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}

