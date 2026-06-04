package org.swp.my_learning_path.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.dto.request.QuizQuestionRequest;
import org.swp.my_learning_path.service.InstructorQuizService;

import java.util.HashMap;
import java.util.Map;
// phase 3 cum 4 lien quan ve tao cac bai test
@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorQuizRestController {

    private final InstructorQuizService quizService;

    // 1. Thêm 1 Câu hỏi (kèm danh sách đáp án) vào một Bài học loại QUIZ
    @PostMapping("/lessons/{lessonId}/questions")
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long lessonId, @Valid @RequestBody QuizQuestionRequest request) {
        try {
            Long questionId = quizService.createQuestionWithAnswers(lessonId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm câu hỏi trắc nghiệm thành công!");
            response.put("questionId", questionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 2. Xóa 1 Câu hỏi
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        try {
            quizService.deleteQuestion(questionId);
            return ResponseEntity.ok("Xóa câu hỏi thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}