package com.swp391.final_project.service;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import com.swp391.final_project.dto.request.CreateUserRequest;
import com.swp391.final_project.entity.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<User> searchUsers(ERole role, EAccountStatus status, String search, Pageable pageable);
    User getUserById(Long userId);
    void toggleUserStatus(Long userId);
    void createUser(CreateUserRequest request);
    void assignRole(Long userId, ERole role);
    void bulkLockUsers(List<Long> userIds, String adminEmail);
    void bulkUnlockUsers(List<Long> userIds, String adminEmail);
}
