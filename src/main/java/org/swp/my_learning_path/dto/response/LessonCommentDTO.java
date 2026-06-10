package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCommentDTO {
    Long commentId;
    String userName;
    String content;
    LocalDateTime createdAt;
    List<LessonCommentDTO> replies;
}