package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateHrRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;
import com.miniorange.recruitmentmanagementservice.entity.Company;
import com.miniorange.recruitmentmanagementservice.entity.User;
import com.miniorange.recruitmentmanagementservice.enums.Role;
import com.miniorange.recruitmentmanagementservice.exception.BadRequestException;
import com.miniorange.recruitmentmanagementservice.exception.ResourceNotFoundException;
import com.miniorange.recruitmentmanagementservice.repository.CompanyRepository;
import com.miniorange.recruitmentmanagementservice.repository.UserRepository;
import com.miniorange.recruitmentmanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .role(request.getRole())
                .enabled(true)
                .build();

        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse createHr(CreateHrRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        User hr = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .role(Role.HR)
                .company(company)
                .enabled(true)
                .build();

        hr = userRepository.save(hr);
        return mapToUserResponse(hr);
    }

    @Override
    public UserResponse createCoo(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User coo = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .mobileNumber(request.getMobileNumber())
                .role(Role.COO)
                .enabled(true)
                .build();

        coo = userRepository.save(coo);
        return mapToUserResponse(coo);
    }

    @Override
    public List<UserResponse> getHrList() {
        return userRepository.findByRole(Role.HR).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getCooList() {
        return userRepository.findByRole(Role.COO).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .companyName(user.getCompany() != null ? user.getCompany().getName() : null)
                .build();
    }
}

