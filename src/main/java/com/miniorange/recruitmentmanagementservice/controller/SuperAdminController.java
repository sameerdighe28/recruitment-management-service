package com.miniorange.recruitmentmanagementservice.controller;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateCompanyRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CompanyResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.MessageResponse;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;
import com.miniorange.recruitmentmanagementservice.service.CompanyService;
import com.miniorange.recruitmentmanagementservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final CompanyService companyService;
    private final UserService userService;

    /**
     * Create/enlist a new company
     */
    @PostMapping("/companies")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a company
     */
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<MessageResponse> deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(new MessageResponse("Company deleted successfully"));
    }

    /**
     * Get all companies
     */
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    /**
     * Create a COO user
     */
    @PostMapping("/coo")
    public ResponseEntity<UserResponse> createCoo(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userService.createCoo(request);
        return ResponseEntity.ok(response);
    }

    /**
     * List all COO users
     */
    @GetMapping("/coo")
    public ResponseEntity<List<UserResponse>> getAllCoos() {
        return ResponseEntity.ok(userService.getCooList());
    }
}

