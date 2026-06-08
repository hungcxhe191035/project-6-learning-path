package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
//cum2 phase 3 Cho phép tạo các chương học
@Data
public class SectionRequest {
    @NotBlank(message = "Tên chương không được để trống")
    private String title;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer displayOrder;
}