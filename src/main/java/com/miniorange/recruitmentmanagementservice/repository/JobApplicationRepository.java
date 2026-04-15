package com.miniorange.recruitmentmanagementservice.repository;

import com.miniorange.recruitmentmanagementservice.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByJobId(UUID jobId);

    List<JobApplication> findByCandidateProfileId(UUID candidateProfileId);

    boolean existsByJobIdAndCandidateProfileId(UUID jobId, UUID candidateProfileId);
}

