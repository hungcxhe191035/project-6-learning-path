package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.constant.EFilePurpose;
import org.swp.my_learning_path.constant.EFileType;
import org.swp.my_learning_path.dto.request.ChangePasswordRequest;
import org.swp.my_learning_path.dto.request.UpdateProfileRequest;
import org.swp.my_learning_path.entity.AppFile;
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
    private final S3Service s3Service;
    private final AppFileService appFileService;

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

    // [MỚI] Cập nhật ảnh đại diện
    // Luồng: upload file lên S3 lấy URL tạo AppFile gán vào User.avatar lưu DB
    @Override
    @Transactional
    public void updateAvatar(Long userId, MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new RuntimeException("File ảnh không hợp lệ hoặc rỗng.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        try {
            // Bước 1: Upload ảnh lên S3 nhận về URL công khai
            String fileUrl = s3Service.uploadFile(avatarFile);

            // Bước 2: Lưu thông tin file vào bảng files
            String originalName = avatarFile.getOriginalFilename() != null
                    ? avatarFile.getOriginalFilename() : "avatar.jpg";
            AppFile savedFile = appFileService.saveFileInfo(fileUrl, EFileType.IMAGE, originalName);
            savedFile.setPurpose(EFilePurpose.AVATAR);

            // Bước 3: Gán AppFile mới vào trường avatar của User
            user.setAvatar(savedFile);
            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tải ảnh lên: " + e.getMessage(), e);
        }
    }

    // Đóng tài khoản
    @Override
    @Transactional
    public void closeAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setStatus(org.swp.my_learning_path.constant.EAccountStatus.INACTIVE);
        userRepository.save(user);
    }
}

