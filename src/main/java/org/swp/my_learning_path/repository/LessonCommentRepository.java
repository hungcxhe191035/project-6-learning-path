package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.entity.LessonComment;

import java.util.List;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {

    List<LessonComment> findByLessonAndParentCommentIsNullOrderByCreatedAtDesc(Lesson lesson);

    List<LessonComment> findByParentCommentOrderByCreatedAtAsc(LessonComment parentComment);
}