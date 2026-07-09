package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.swp.my_learning_path.constant.EBlogStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogDTO {
    Long blogId;
    String title;
    String summary;
    String content;
    EBlogStatus status;
    Integer views;
    String coverUrl;
    Long authorId;
    String authorName;
    String authorLetter;
    LocalDateTime createdAt;
    Long lessonId;
    String lessonTitle;
}
