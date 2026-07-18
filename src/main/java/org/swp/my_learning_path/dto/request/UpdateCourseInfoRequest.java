package org.swp.my_learning_path.dto.request;

import lombok.Data;
import java.math.BigDecimal;
// phase 3, cập nhật thông tin khóa học
@Data
public class UpdateCourseInfoRequest {
    private String title;
    private String subtitle;
    private String description;
    private BigDecimal price;
    private Long thumbnailFileId;
    private java.util.List<Long> tagIds;
}