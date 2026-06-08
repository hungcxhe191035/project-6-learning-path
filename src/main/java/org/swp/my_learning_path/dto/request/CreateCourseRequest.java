package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
// lấy tên khóa học từ màn hình
@Getter
@Setter
public class CreateCourseRequest {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;
}