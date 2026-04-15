package com.miniorange.recruitmentmanagementservice.config;

import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.Role;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.mobile}")
    private String adminMobile;

    @Value("${app.admin.name}")
    private String adminName;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName(adminName)
                    .mobileNumber(adminMobile)
                    .role(Role.SUPER_ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("Default SUPER_ADMIN user created with email: {}", adminEmail);
        } else {
            log.info("SUPER_ADMIN user already exists with email: {}", adminEmail);
        }
    }
}

