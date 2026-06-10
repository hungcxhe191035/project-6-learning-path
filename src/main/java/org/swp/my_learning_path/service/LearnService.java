package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.response.LearnCourseDTO;
import org.swp.my_learning_path.dto.response.LessonCommentDTO;

import java.util.List;
import java.util.Map;

public interface LearnService {
    // Lấy toàn bộ dữ liệu trang học
    LearnCourseDTO getLearnData(Long courseId, Long studentId);

    // Đánh dấu hoàn thành bài học (cho VIDEO / ARTICLE)
    void completeLesson(Long lessonId, Long studentId);

    // Lấy danh sách câu hỏi quiz (không lộ đáp án đúng)
    List<Map<String, Object>> getQuizData(Long lessonId);

    // Nộp bài quiz — trả về % đúng
    Map<String, Object> submitQuiz(Long lessonId, Long studentId, Map<Long, Long> answers);

    // Lấy danh sách comment Q&A của bài học
    List<LessonCommentDTO> getComments(Long lessonId);

    // Đăng comment / trả lời
    LessonCommentDTO addComment(Long lessonId, Long studentId, String content, Long parentCommentId);

    // Gửi đánh giá khoá học
    void submitFeedback(Long courseId, Long studentId, int rating, String comment);

    Map<String, Object> getLessonDetail(Long lessonId);

}