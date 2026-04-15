package com.miniorange.recruitmentmanagementservice.repository;

import com.miniorange.recruitmentmanagementservice.entity.Job;
import com.miniorange.recruitmentmanagementservice.enums.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByCompanyId(UUID companyId);

    List<Job> findByCategory(JobCategory category);

    List<Job> findByPostedById(UUID userId);
}

