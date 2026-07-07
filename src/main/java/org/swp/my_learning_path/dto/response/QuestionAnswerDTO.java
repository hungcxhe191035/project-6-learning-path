package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAnswerDTO {
    Long answerId;
    Long senderId;
    String senderName;
    String senderAvatar;
    String senderRole; // "STUDENT" hoặc "INSTRUCTOR"
    String content;
    LocalDateTime createdAt;
}
