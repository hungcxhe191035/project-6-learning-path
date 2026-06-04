package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.QuizAnswer;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    // Hàm này dùng để xóa tất cả đáp án khi mình muốn xóa một câu hỏi
    void deleteByQuestion(org.swp.my_learning_path.entity.QuizQuestion question);
}