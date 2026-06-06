package org.swp.my_learning_path.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathDto {

    private Long pathId;

    private String title;

    private boolean selected;
}