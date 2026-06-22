package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.dto.response.LessonCommentDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.LearnService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learn")
@RequiredArgsConstructor
public class LearnRestController {

    private final LearnService learnService;

    // Đánh dấu hoàn thành VIDEO hoặc ARTICLE
    @PostMapping("/complete/{lessonId}")
    public ResponseEntity<?> complete(@PathVariable Long lessonId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        learnService.completeLesson(lessonId, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Lấy câu hỏi quiz (không lộ đáp án đúng)
    @GetMapping("/{courseId}/quiz/{lessonId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long courseId,
                                     @PathVariable Long lessonId) {
        return ResponseEntity.ok(learnService.getQuizData(lessonId));
    }

    // Nộp bài quiz
    @PostMapping("/{courseId}/quiz/{lessonId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long courseId,
                                        @PathVariable Long lessonId,
                                        @RequestBody Map<Long, Long> answers,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(learnService.submitQuiz(lessonId, userDetails.getUserId(), answers));
    }

    // Lấy Q&A của bài học
    @GetMapping("/comments/{lessonId}")
    public ResponseEntity<List<LessonCommentDTO>> getComments(@PathVariable Long lessonId) {
        return ResponseEntity.ok(learnService.getComments(lessonId));
    }

    // Đăng câu hỏi hoặc trả lời
    @PostMapping("/comments/{lessonId}")
    public ResponseEntity<LessonCommentDTO> addComment(@PathVariable Long lessonId,
                                                       @RequestBody Map<String, Object> body,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        String content = (String) body.get("content");
        Long parentId = body.get("parentCommentId") != null
                ? Long.valueOf(body.get("parentCommentId").toString()) : null;
        return ResponseEntity.ok(learnService.addComment(lessonId, userDetails.getUserId(), content, parentId));
    }

    // Gửi đánh giá khoá học
    @PostMapping("/{courseId}/feedback")
    public ResponseEntity<?> submitFeedback(@PathVariable Long courseId,
                                            @RequestBody Map<String, Object> body,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int rating = Integer.parseInt(body.get("rating").toString());
        String comment = (String) body.get("comment");
        learnService.submitFeedback(courseId, userDetails.getUserId(), rating, comment);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Lấy chi tiết 1 bài học (title, videoUrl, articleContent)
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getLessonDetail(@PathVariable Long lessonId) {
        // Tìm lesson và trả về thông tin cơ bản
        // Bạn cần inject LessonRepository vào LearnRestController
        return ResponseEntity.ok(learnService.getLessonDetail(lessonId));
    }

    // Lấy câu hỏi quiz gắn theo mốc thời gian của video
    @GetMapping("/lesson/{lessonId}/video-quizzes")
    public ResponseEntity<?> getVideoTimestampQuizzes(@PathVariable Long lessonId) {
        return ResponseEntity.ok(learnService.getVideoTimestampQuizzes(lessonId));
    }
}