package org.swp.my_learning_path.dto.request;

import org.swp.my_learning_path.constant.EApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO dùng khi Admin duyệt hoặc từ chối đơn xin trở thành giảng viên.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewApplicationRequest {

    @NotNull(message = "Quyết định không được để trống")
    EApplicationStatus decision; // Chỉ chấp nhận APPROVED hoặc REJECTED

    // Bắt buộc khi decision = REJECTED, tùy chọn khi APPROVED
    String reviewNote;
}
