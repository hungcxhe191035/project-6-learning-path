package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * DTO dùng khi STUDENT nộp đơn xin trở thành INSTRUCTOR.
 * CV file được truyền riêng qua MultipartFile trong Controller.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitApplicationRequest {

    @NotBlank(message = "Tiêu đề nghề nghiệp không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    String headline;

    @NotBlank(message = "Giới thiệu bản thân không được để trống")
    @Size(max = 2000, message = "Giới thiệu không được vượt quá 2000 ký tự")
    String bio;

    @NotBlank(message = "Lý do muốn trở thành giảng viên không được để trống")
    @Size(max = 2000, message = "Lý do không được vượt quá 2000 ký tự")
    String motivation;

    @Size(max = 500, message = "LinkedIn URL không được vượt quá 500 ký tự")
    String linkedinUrl;

    // Danh sách tag IDs được chọn (không giới hạn số lượng, có thể rỗng)
    List<Long> tagIds;
}

