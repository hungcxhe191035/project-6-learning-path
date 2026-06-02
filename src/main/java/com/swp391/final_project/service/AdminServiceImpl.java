package com.swp391.final_project.service;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import com.swp391.final_project.dto.request.CreateUserRequest;
import com.swp391.final_project.dto.request.UpdateUserRequest;
import com.swp391.final_project.entity.User;
import com.swp391.final_project.entity.Wallet;
import com.swp391.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> searchUsers(ERole role, EAccountStatus status, String search, Pageable pageable) {
        String searchParam = (search == null || search.trim().isEmpty()) ? null : search.trim();
        return userRepository.searchUsers(role, status, searchParam, pageable);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + userId));
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        if (user.getStatus() == EAccountStatus.ACTIVE) {
            user.setStatus(EAccountStatus.INACTIVE);
        } else {
            user.setStatus(EAccountStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPlainPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getStatus())
                .build();
        user.setDeleteFlag(false);

        // Tự động tạo ví trống
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        user.setWallet(wallet);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UpdateUserRequest request) {
        User user = getUserById(userId);

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        user.setDeleteFlag(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void bulkLockUsers(List<Long> userIds, String adminEmail) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            // Bỏ qua tài khoản đã soft-delete và bảo vệ admin không tự khóa mình
            if (user.isDeleteFlag() || user.getEmail().equalsIgnoreCase(adminEmail)) {
                continue;
            }
            user.setStatus(EAccountStatus.INACTIVE);
        }
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void bulkUnlockUsers(List<Long> userIds, String adminEmail) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            if (user.isDeleteFlag() || user.getEmail().equalsIgnoreCase(adminEmail)) {
                continue;
            }
            user.setStatus(EAccountStatus.ACTIVE);
        }
        userRepository.saveAll(users);
    }

    @Override
    @Transactional
    public void bulkDeleteUsers(List<Long> userIds, String adminEmail) {
        if (userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(adminEmail)) {
                continue;
            }
            user.setDeleteFlag(true);
        }
        userRepository.saveAll(users);
    }
}
