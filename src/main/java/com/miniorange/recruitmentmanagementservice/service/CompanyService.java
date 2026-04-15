package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateCompanyRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CompanyResponse;

import java.util.List;
import java.util.UUID;

public interface CompanyService {

    CompanyResponse createCompany(CreateCompanyRequest request);

    void deleteCompany(UUID companyId);

    CompanyResponse getCompany(UUID companyId);

    List<CompanyResponse> getAllCompanies();

    List<CompanyResponse> searchCompanies(String name);
}

