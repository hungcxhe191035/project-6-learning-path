package org.swp.my_learning_path.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.dto.request.LoginRequest;
import org.swp.my_learning_path.dto.request.RegisterRequest;
import org.swp.my_learning_path.dto.response.UserResponse;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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

    @Override
    public void forgotPassword(String email) {
        // 1. Kiểm tra email có trong DB không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        // 2. Tự sinh mật khẩu mới ngẫu nhiên 8 ký tự
        String newPassword = generateRandomPassword();
        // 3. Mã hoá BCrypt rồi lưu vào DB
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // 4. Gửi mật khẩu CHƯA mã hoá qua email cho user
        emailService.sendNewPasswordEmail(email, newPassword);
    }

    @Override
    public void register(RegisterRequest request) {
        // 1. Kiểm tra mật khẩu khớp
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu và xác nhận mật khẩu không khớp!");
        }

        // 2. Kiểm tra email đã được sử dụng chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác!");
        }

        // 3. Tạo user mới
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(ERole.STUDENT)
                .status(EAccountStatus.ACTIVE)
                .build();
        user.setDeleteFlag(false);

        // 4. Tạo ví tiền mặc định cho User
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        wallet.setDeleteFlag(false);

        user.setWallet(wallet);

        // 5. Lưu vào DB (cascade sẽ tự lưu Wallet)
        userRepository.save(user);
    }

    // Hàm tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
