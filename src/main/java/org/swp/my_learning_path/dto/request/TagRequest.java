package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO dùng khi Admin tạo hoặc sửa Tag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagRequest {

    @NotBlank(message = "Tên tag không được để trống")
    @Size(max = 255, message = "Tên tag không được vượt quá 255 ký tự")
    String tagName;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    String description;
}
