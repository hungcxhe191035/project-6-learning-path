package org.swp.my_learning_path.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword;   // Mật khẩu hiện tại
    private String newPassword;       // Mật khẩu mới
    private String confirmPassword;   // Xác nhận mật khẩu mới
}
