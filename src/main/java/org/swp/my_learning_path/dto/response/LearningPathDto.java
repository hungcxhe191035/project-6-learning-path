package org.swp.my_learning_path.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathDto {

    private Long pathId;

    private String title;

    private String description;

    private boolean selected;

    private Integer courseCount;

    private String thumbnailUrl;
}