package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionDTO {
    Long sectionId;
    String title;
    Integer displayOrder;
    List<LessonDTO> lessons; // danh sách bài học trong phần này
}
