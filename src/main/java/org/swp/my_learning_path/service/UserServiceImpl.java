package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.dto.request.ChangePasswordRequest;
import org.swp.my_learning_path.dto.request.UpdateProfileRequest;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.entity.UserProfile;
import org.swp.my_learning_path.repository.UserProfileRepository;
import org.swp.my_learning_path.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    //  Lấy User theo email
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + email));
    }

    // Lấy UserProfile theo userId
    @Override
    public UserProfile getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserUserId(userId).orElse(null);
    }

    // Cập nhật thông tin cá nhân
    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBankName(request.getBankName());
        user.setBankCode(request.getBankCode());
        user.setBankAccountNumber(request.getBankAccountNumber());
        user.setBankAccountHolder(request.getBankAccountHolder());
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUserUserId(userId)
                .orElse(UserProfile.builder().user(user).build());

        profile.setBio(request.getBio());
        profile.setHeadline(request.getHeadline());
        profile.setFacebookUrl(request.getFacebookUrl());
        profile.setYoutubeUrl(request.getYoutubeUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        userProfileRepository.save(profile);
    }

    //  Đổi mật khẩu
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng!");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
