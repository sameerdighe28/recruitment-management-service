package com.miniorange.recruitmentmanagementservice.repository;

import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}

