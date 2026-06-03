package com.swp391.final_project.dto.request;

import com.swp391.final_project.constant.ERole;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO phục vụ riêng cho việc Admin gán vai trò mới cho người dùng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignRoleRequest {

    @NotNull(message = "Vai trò không được để trống")
    ERole role;
}
