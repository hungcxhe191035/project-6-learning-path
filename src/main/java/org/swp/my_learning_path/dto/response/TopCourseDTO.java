package org.swp.my_learning_path.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCourseDTO {
    private Long courseId;
    private String title;
    private String instructorName;
    private BigDecimal price;
    private Long salesCount;
    private BigDecimal averageRating;
}
