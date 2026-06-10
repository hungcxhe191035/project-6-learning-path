package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Enrollment;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.entity.LessonProgress;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByEnrollmentAndLesson(Enrollment enrollment, Lesson lesson);

    List<LessonProgress> findByEnrollment(Enrollment enrollment);

    long countByEnrollmentAndIsCompleted(Enrollment enrollment, Boolean isCompleted);
}