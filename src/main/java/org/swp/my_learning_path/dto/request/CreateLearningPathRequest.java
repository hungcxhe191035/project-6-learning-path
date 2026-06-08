package org.swp.my_learning_path.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearningPathRequest {

    @NotBlank
    private String title;

    private String description;
}