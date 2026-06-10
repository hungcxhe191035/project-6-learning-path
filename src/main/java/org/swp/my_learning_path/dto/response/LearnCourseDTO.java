package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnCourseDTO {
    Long courseId;
    String title;
    String thumbnailUrl;
    String instructorName;
    List<SectionLearnDTO> sections;
    int progressPercent;     // % hoàn thành toàn bộ khoá học
    Long firstUnlockedLessonId; // bài học đầu tiên chưa hoàn thành
}