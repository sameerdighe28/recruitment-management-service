package com.miniorange.recruitmentmanagementservice.repository;

import com.miniorange.recruitmentmanagementservice.entity.CandidateProfile;
import com.miniorange.recruitmentmanagementservice.enums.CandidateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {

    Optional<CandidateProfile> findByUserId(UUID userId);

    List<CandidateProfile> findByCategory(CandidateCategory category);

    boolean existsByUserId(UUID userId);
}

