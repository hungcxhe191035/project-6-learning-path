package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonLearnDTO {
    Long lessonId;
    String title;
    String lessonType;       // VIDEO, QUIZ, ARTICLE
    Integer displayOrder;
    Integer durationSeconds;
    String videoUrl;         // URL video nếu là VIDEO
    String articleContent;   // Nội dung nếu là ARTICLE
    boolean isCompleted;     // Học sinh đã hoàn thành chưa?
    boolean isLocked;        // Bị khóa vì chưa học bài trước?
}