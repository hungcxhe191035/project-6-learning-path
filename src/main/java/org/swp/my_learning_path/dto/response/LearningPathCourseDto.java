package org.swp.my_learning_path.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathCourseDto {

    private Long courseId;

    private Integer displayOrder;

    private String title;

    private String thumbnailUrl;

    private String shortDescription;

    private BigDecimal averageRating;

    private Integer totalStudents;

    private String instructorName;
}