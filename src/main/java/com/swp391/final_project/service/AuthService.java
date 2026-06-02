package com.swp391.final_project.service;

import com.swp391.final_project.dto.request.LoginRequest;
import com.swp391.final_project.dto.response.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}
