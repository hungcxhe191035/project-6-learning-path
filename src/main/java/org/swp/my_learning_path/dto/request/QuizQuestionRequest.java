package org.swp.my_learning_path.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
// hưứng thông tin từ câu hỏi chứa các đáp án ABCD
@Data
public class QuizQuestionRequest {
    @NotBlank(message = "Câu hỏi không được để trống")
    private String questionText;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    private Integer displayOrder;

    // Danh sách các đáp án (Thường là 4 đáp án A,B,C,D)
    @Valid
    private List<QuizAnswerRequest> answers;
}