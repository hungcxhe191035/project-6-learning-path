package service;

import com.hsf302.final_project.dto.request.LoginRequest;
import com.hsf302.final_project.dto.response.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}
