package org.swp.my_learning_path.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    // Thông tin cơ bản (từ bảng users)
    private String fullName;
    private String phone;

    // Thông tin mở rộng (từ bảng user_profile)
    private String bio;
    private String headline;
    private String facebookUrl;
    private String youtubeUrl;
    private String linkedinUrl;

    // Thông tin ngân hàng (từ bảng users)
    private String bankName;
    private String bankCode;
    private String bankAccountNumber;
    private String bankAccountHolder;
}
