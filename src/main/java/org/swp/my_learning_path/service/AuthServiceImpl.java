package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.dto.request.LoginRequest;
import org.swp.my_learning_path.dto.response.UserResponse;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse login(LoginRequest request) {
        // tìm user theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Email does not exist")
                );
        // kiểm tra trạng thái tài khoản
        if (user.getStatus() != EAccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        // kiểm tra password BCrypt
        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );
        if (!matches) {
            throw new RuntimeException("Invalid password");
        }
        // login thành công
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
