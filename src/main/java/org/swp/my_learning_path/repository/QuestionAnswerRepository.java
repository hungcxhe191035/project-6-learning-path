package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.QuestionAnswer;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    // Lấy tất cả các câu trả lời/giải thích thuộc về 1 câu hỏi (theo thứ tự thời gian tăng dần)
    List<QuestionAnswer> findByQuestion_QuestionIdOrderByCreatedAtAsc(Long questionId);
}
