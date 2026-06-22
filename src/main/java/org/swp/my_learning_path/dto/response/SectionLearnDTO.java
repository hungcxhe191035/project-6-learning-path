package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionLearnDTO {
    Long sectionId;
    String title;
    Integer displayOrder;
    List<LessonLearnDTO> lessons;
    long completedCount;   // số bài đã học trong chương
    long totalCount;       // tổng số bài trong chương
}