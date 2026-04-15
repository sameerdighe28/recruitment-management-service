package com.miniorange.recruitmentmanagementservice.controller;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateCompanyRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.CreateHrRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CompanyResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;
import com.miniorange.recruitmentmanagementservice.service.CompanyService;
import com.miniorange.recruitmentmanagementservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coo")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COO')")
public class CooController {

    private final CompanyService companyService;
    private final UserService userService;

    /**
     * COO can enlist (create) a company but cannot delete
     */
    @PostMapping("/companies")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.ok(response);
    }

    /**
     * List all companies
     */
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    /**
     * COO can onboard/create HR users
     */
    @PostMapping("/hr")
    public ResponseEntity<UserResponse> createHr(@Valid @RequestBody CreateHrRequest request) {
        UserResponse response = userService.createHr(request);
        return ResponseEntity.ok(response);
    }

    /**
     * List all HR users
     */
    @GetMapping("/hr")
    public ResponseEntity<List<UserResponse>> getAllHrs() {
        return ResponseEntity.ok(userService.getHrList());
    }
}

