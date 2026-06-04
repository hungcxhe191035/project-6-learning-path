package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.swp.my_learning_path.constant.ELessonType;
//này hứng tất cả các loại dữ liệu, tùy xem giảng viên chọn tạo Video hay tạo Bài viết:
@Data
public class LessonRequest {
    @NotBlank(message = "Tên bài học không được để trống")
    private String title;

    @NotNull(message = "Loại bài học không được để trống")
    private ELessonType lessonType; // Chứa 1 trong 3 chữ: VIDEO, QUIZ, ARTICLE

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer displayOrder;

    // Dành riêng cho loại VIDEO
    private Long videoFileId;
    private Integer durationSeconds;

    // Dành riêng cho loại ARTICLE
    private String articleContent;
}