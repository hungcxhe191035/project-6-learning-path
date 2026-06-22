package org.swp.my_learning_path.dto.request;

import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO dùng khi Admin tạo mới tài khoản người dùng.
 * Tránh mass assignment vulnerability khi bind form trực tiếp vào Entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 100, message = "Mật khẩu phải có từ 8 đến 100 ký tự")
    String plainPassword;

    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "Số điện thoại không đúng định dạng (phải gồm 10 chữ số, bắt đầu bằng 0 hoặc +84)")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    String phone;

    @NotNull(message = "Vai trò không được để trống")
    ERole role;

    @NotNull(message = "Trạng thái không được để trống")
    EAccountStatus status;
}
