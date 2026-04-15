package com.miniorange.recruitmentmanagementservice.service;

import com.miniorange.recruitmentmanagementservice.dto.request.CreateHrRequest;
import com.miniorange.recruitmentmanagementservice.dto.request.RegisterUserRequest;
import com.miniorange.recruitmentmanagementservice.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(RegisterUserRequest request);

    UserResponse createHr(CreateHrRequest request);

    UserResponse createCoo(RegisterUserRequest request);

    List<UserResponse> getHrList();

    List<UserResponse> getCooList();
}

