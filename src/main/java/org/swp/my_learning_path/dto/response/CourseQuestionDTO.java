package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.swp.my_learning_path.constant.EQuestionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseQuestionDTO {
    Long questionId;
    Long studentId;
    String studentName;
    String studentAvatar;
    Long courseId;
    String courseTitle;
    Long lessonId;
    String lessonTitle;
    String title;
    String content;
    EQuestionStatus status;
    LocalDateTime createdAt;
    List<QuestionAnswerDTO> answers;
}
