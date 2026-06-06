package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.QuizQuestion;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    java.util.List<QuizQuestion> findByLessonOrderByDisplayOrderAsc(org.swp.my_learning_path.entity.Lesson lesson);
    void deleteByLesson(org.swp.my_learning_path.entity.Lesson lesson);
}