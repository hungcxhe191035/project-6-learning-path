package org.swp.my_learning_path.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CourseFeedback;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseFeedbackRepository extends JpaRepository<CourseFeedback, Long> {
    // Lấy đánh giá của 1 khoá học, mới nhất lên trước
    List<CourseFeedback> findByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);
    Optional<CourseFeedback> findByCourse_CourseIdAndStudent_UserId(Long courseId, Long studentId);
}
