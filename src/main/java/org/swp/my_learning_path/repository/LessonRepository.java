package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Lesson;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // Hàm này giúp lấy danh sách Bài giảng của 1 Chương và xếp thứ tự
    List<Lesson> findBySection_SectionIdOrderByDisplayOrderAsc(Long sectionId);
}