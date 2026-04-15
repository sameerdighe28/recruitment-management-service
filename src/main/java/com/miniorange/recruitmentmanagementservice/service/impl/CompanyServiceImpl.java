package com.miniorange.recruitmentmanagementservice.service.impl;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateCompanyRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.CompanyResponse;
import com.miniorange.recruitmentmanagementservice.entity.Company;
import com.miniorange.recruitmentmanagementservice.exception.BadRequestException;
import com.miniorange.recruitmentmanagementservice.exception.ResourceNotFoundException;
import com.miniorange.recruitmentmanagementservice.repository.CompanyRepository;
import com.miniorange.recruitmentmanagementservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Company with name '" + request.getName() + "' already exists");
        }

        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .website(request.getWebsite())
                .build();

        company = companyRepository.save(company);
        return mapToCompanyResponse(company);
    }

    @Override
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        companyRepository.delete(company);
    }

    @Override
    public CompanyResponse getCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        return mapToCompanyResponse(company);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyResponse> searchCompanies(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    private CompanyResponse mapToCompanyResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .website(company.getWebsite())
                .build();
    }
}

