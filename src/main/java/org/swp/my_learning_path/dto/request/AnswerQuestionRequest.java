package org.swp.my_learning_path.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerQuestionRequest {
    Long questionId;
    String content;
}
