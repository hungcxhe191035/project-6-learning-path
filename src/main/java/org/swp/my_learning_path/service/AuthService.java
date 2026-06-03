package org.swp.my_learning_path.service;


import org.swp.my_learning_path.dto.request.LoginRequest;
import org.swp.my_learning_path.dto.response.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}
