package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ELessonType;
import org.swp.my_learning_path.dto.request.QuizAnswerRequest;
import org.swp.my_learning_path.dto.request.QuizQuestionRequest;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.entity.QuizAnswer;
import org.swp.my_learning_path.entity.QuizQuestion;
import org.swp.my_learning_path.repository.LessonRepository;
import org.swp.my_learning_path.repository.QuizAnswerRepository;
import org.swp.my_learning_path.repository.QuizQuestionRepository;

@Service
@RequiredArgsConstructor
public class InstructorQuizService {

    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAnswerRepository answerRepository;

    @Transactional
    public Long createQuestionWithAnswers(Long lessonId, QuizQuestionRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        // Bắt lỗi: Bài giảng phải là định dạng QUIZ thì mới cho nhét câu hỏi vào
        if (lesson.getLessonType() != ELessonType.QUIZ) {
            throw new RuntimeException("Bài học này không phải là bài Trắc nghiệm (QUIZ)!");
        }

        // 1. Lưu câu hỏi vào Database
        QuizQuestion question = QuizQuestion.builder()
                .lesson(lesson)
                .questionText(request.getQuestionText())
                .displayOrder(request.getDisplayOrder())
                .build();
        question = questionRepository.save(question);

        // 2. Chạy vòng lặp lưu tất cả các đáp án (A,B,C,D...) gửi kèm
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            for (QuizAnswerRequest ansReq : request.getAnswers()) {
                QuizAnswer answer = QuizAnswer.builder()
                        .question(question)
                        .answerText(ansReq.getAnswerText())
                        .isCorrect(ansReq.getIsCorrect())
                        .displayOrder(ansReq.getDisplayOrder())
                        .build();
                answerRepository.save(answer);
            }
        }

        return question.getQuestionId();
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi!"));

        // Phải xóa các đáp án trước, rồi mới xóa câu hỏi được (Luật của Database)
        answerRepository.deleteByQuestion(question);
        questionRepository.delete(question);
    }
}