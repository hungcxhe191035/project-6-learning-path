package org.swp.my_learning_path.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCardDTO {
    Long courseId;
    String title;           // Tên khoá học
    String subtitle;        // Mô tả ngắn
    String instructorName;  // Tên giảng viên
    BigDecimal price;       // Giá tiền
    BigDecimal averageRating; // Điểm đánh giá (0.0 - 5.0)
    Integer totalReviews;   // Tổng số đánh giá
    String thumbnailUrl;    // Đường dẫn ảnh thumbnail
    Boolean isEnrolled;     // Đã mua khoá học hay chưa
    java.util.List<String> tags; // Danh sách các nhãn tag của khoá học
}