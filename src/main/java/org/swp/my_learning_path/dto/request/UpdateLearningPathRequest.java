package org.swp.my_learning_path.dto.request;

import lombok.Data;

@Data
public class UpdateLearningPathRequest {

    private String title;

    private String description;
}
