package org.swp.my_learning_path.service;

import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.dto.request.ChangePasswordRequest;
import org.swp.my_learning_path.dto.request.UpdateProfileRequest;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.entity.UserProfile;

public interface UserService {

    // Lấy thông tin User theo email (dùng sau khi đăng nhập)
    User getUserByEmail(String email);

    // Lấy UserProfile theo userId (có thể null nếu chưa tạo)
    UserProfile getProfileByUserId(Long userId);

    // Cập nhật thông tin cá nhân
    void updateProfile(Long userId, UpdateProfileRequest request);

    // Đổi mật khẩu
    void changePassword(Long userId, ChangePasswordRequest request);

    // [MỚI] Cập nhật ảnh đại diện: upload lên S3, lưu AppFile, gán vào User
    void updateAvatar(Long userId, MultipartFile avatarFile);

    // Đóng tài khoản
    void closeAccount(Long userId);
}

