package com.swp391.final_project.service;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import com.swp391.final_project.entity.User;
import com.swp391.final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.swp391.final_project.entity.Wallet;
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
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
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
    public void createUser(User user, String plainPassword) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setDeleteFlag(false);

        // Auto create empty wallet
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        user.setWallet(wallet);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, User userDetails, String newPassword) {
        User user = getUserById(userId);

        if (!user.getEmail().equalsIgnoreCase(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
        }

        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        user.setStatus(userDetails.getStatus());

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
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
            if (user.getEmail().equalsIgnoreCase(adminEmail)) {
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
            if (user.getEmail().equalsIgnoreCase(adminEmail)) {
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
