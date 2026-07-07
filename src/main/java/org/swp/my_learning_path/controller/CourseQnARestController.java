package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.dto.request.AnswerQuestionRequest;
import org.swp.my_learning_path.dto.request.AskQuestionRequest;
import org.swp.my_learning_path.dto.response.CourseQuestionDTO;
import org.swp.my_learning_path.dto.response.QuestionAnswerDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CourseQnAService;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class CourseQnARestController {

    private final CourseQnAService qnaService;

    // API 1: Học viên gửi câu hỏi thắc mắc bài học mới
    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(
            @RequestBody AskQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để gửi thắc mắc!");
        }

        try {
            CourseQuestionDTO response = qnaService.askQuestion(userDetails.getUserId(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API 2: Trả lời / Giải thích câu hỏi bài học
    @PostMapping("/answer")
    public ResponseEntity<?> answerQuestion(
            @RequestBody AnswerQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập!");
        }

        try {
            QuestionAnswerDTO response = qnaService.answerQuestion(userDetails.getUserId(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API 3: Lấy tất cả câu hỏi thuộc về một bài học
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getQuestionsByLesson(@PathVariable Long lessonId) {
        try {
            List<CourseQuestionDTO> questions = qnaService.getQuestionsByLesson(lessonId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API 4: Đóng câu hỏi sau khi học viên đã hiểu bài
    @PostMapping("/close/{questionId}")
    public ResponseEntity<?> closeQuestion(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập!");
        }

        try {
            qnaService.closeQuestion(questionId, userDetails.getUserId());
            return ResponseEntity.ok("Đã đóng thắc mắc thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
