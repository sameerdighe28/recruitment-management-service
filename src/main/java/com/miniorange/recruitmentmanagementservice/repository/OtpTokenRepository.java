package com.miniorange.recruitmentmanagementservice.repository;

import com.miniorange.recruitmentmanagementservice.entity.OtpToken;
import com.miniorange.recruitmentmanagementservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    Optional<OtpToken> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    Optional<OtpToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
}

