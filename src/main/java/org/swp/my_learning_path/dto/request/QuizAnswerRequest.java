package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
// dùng để hứng thông tin từ đáp án cụm 4 phase 3  API quản lý tn
@Data
public class QuizAnswerRequest {
    @NotBlank(message = "Đáp án không được để trống")
    private String answerText;

    @NotNull(message = "Phải chỉ định đáp án đúng/sai")
    private Boolean isCorrect;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer displayOrder;
}