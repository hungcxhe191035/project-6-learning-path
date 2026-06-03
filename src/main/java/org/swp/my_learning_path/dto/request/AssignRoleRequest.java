package org.swp.my_learning_path.dto.request;

import org.swp.my_learning_path.constant.ERole;
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
