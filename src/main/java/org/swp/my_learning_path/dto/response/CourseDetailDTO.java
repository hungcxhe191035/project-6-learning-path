package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailDTO {
    Long courseId;
    Long instructorId;   // ID giảng viên — dùng để kiểm tra ownership
    String title;
    String subtitle;
    String description;
    BigDecimal price;
    String thumbnailUrl;
    BigDecimal averageRating;
    Integer totalReviews;
    Integer totalStudents;
    String instructorName;
    Long courseVersionId;
    List<SectionDTO> sections;   // danh sách phần học
    List<FeedbackDTO> feedbacks; // danh sách đánh giá
    Boolean isBlocked;
    String blockReason;
}
