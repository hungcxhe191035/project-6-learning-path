package org.swp.my_learning_path.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.dto.request.LessonRequest;
import org.swp.my_learning_path.service.InstructorLessonService;

import java.util.HashMap;
import java.util.Map;

// Phase 3 cụm 3: Quản lý Bài giảng (Lesson)
@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorLessonRestController {

    private final InstructorLessonService lessonService;

    // 1. API Tạo bài học mới (Video, Bài viết, hoặc Trắc nghiệm)
    @PostMapping("/sections/{sectionId}/lessons")
    public ResponseEntity<?> createLesson(@PathVariable Long sectionId, @Valid @RequestBody LessonRequest request) {
        try {
            Long lessonId = lessonService.createLesson(sectionId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo bài học thành công!");
            response.put("lessonId", lessonId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 2. API Cập nhật thông tin cơ bản của Bài học (Tên, Thứ tự)
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonRequest request) {
        try {
            lessonService.updateLesson(lessonId, request);
            return ResponseEntity.ok("Cập nhật bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 3. API Xóa bài học
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId) {
        try {
            lessonService.deleteLesson(lessonId);
            return ResponseEntity.ok("Xóa bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // ================== CÁC API MỚI DÀNH CHO PHASE 6 ================== //

    // 4. API Tải File Video lên S3
    @PostMapping("/lessons/{lessonId}/video")
    public ResponseEntity<?> uploadLessonVideo(@PathVariable Long lessonId, @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = lessonService.uploadLessonVideo(lessonId, file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tải video lên S3 thành công!");
            response.put("fileUrl", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tải video: " + e.getMessage());
        }
    }

    // 5. API Cập nhật nội dung HTML của Bài viết (Article)
    @PutMapping("/lessons/{lessonId}/article")
    public ResponseEntity<?> updateLessonArticle(@PathVariable Long lessonId, @RequestBody Map<String, String> payload) {
        try {
            String htmlContent = payload.get("content");
            lessonService.updateLessonArticle(lessonId, htmlContent);
            return ResponseEntity.ok("Lưu bài viết thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lưu bài viết: " + e.getMessage());
        }
    }

    /**
     * 6. API Lưu toàn bộ dữ liệu của Bài tập Trắc nghiệm (Quiz).
     * @param lessonId: ID của bài giảng cần lưu
     * @param payload: Mảng JSON chứa danh sách các Câu hỏi (Kèm theo danh sách Đáp án bên trong từng câu)
     * Chức năng: Đẩy toàn bộ dữ liệu xuống Service để xóa dữ liệu cũ và lưu lại cấu trúc câu hỏi mới.
     */
    @PutMapping("/lessons/{lessonId}/quiz")
    public ResponseEntity<?> updateLessonQuiz(@PathVariable Long lessonId, @Valid @RequestBody java.util.List<org.swp.my_learning_path.dto.request.QuizQuestionRequest> payload) {
        try {
            lessonService.saveQuizContent(lessonId, payload);
            return ResponseEntity.ok("Lưu bài tập trắc nghiệm thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lưu bài tập trắc nghiệm: " + e.getMessage());
        }
    }

    /**
     * 7. API Lấy chi tiết thông tin của 1 Bài giảng bất kỳ để hiển thị lại lên Giao diện.
     * - Nếu là bài VIDEO: Trả về link Video
     * - Nếu là bài ARTICLE: Trả về nội dung văn bản HTML
     * - Nếu là bài QUIZ: Trả về Cấu trúc cây (Tree) gồm Câu hỏi -> Các đáp án
     */
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<?> getLessonDetail(@PathVariable Long lessonId) {
        try {
            // Lấy thông tin cơ bản của bài giảng
            org.swp.my_learning_path.entity.Lesson lesson = lessonService.getLessonById(lessonId);
            Map<String, Object> response = new HashMap<>();
            response.put("lessonId", lesson.getLessonId());
            response.put("lessonType", lesson.getLessonType());
            response.put("articleContent", lesson.getArticleContent());

            // Nếu có Video đính kèm thì lấy link S3 trả về
            if(lesson.getVideo() != null) {
                response.put("videoUrl", lesson.getVideo().getFileUrl());
            }

            // Xử lý riêng cho loại Trắc nghiệm: Gom nhóm Câu hỏi và Đáp án
            if(lesson.getLessonType() == org.swp.my_learning_path.constant.ELessonType.QUIZ) {
                // Lấy danh sách câu hỏi
                java.util.List<org.swp.my_learning_path.entity.QuizQuestion> questions = lessonService.getQuizQuestionsByLesson(lesson);
                java.util.List<Map<String, Object>> questionsResponse = new java.util.ArrayList<>();

                for (org.swp.my_learning_path.entity.QuizQuestion q : questions) {
                    Map<String, Object> qMap = new HashMap<>();
                    qMap.put("questionId", q.getQuestionId());
                    qMap.put("questionText", q.getQuestionText());
                    qMap.put("displayOrder", q.getDisplayOrder());

                    // Lấy toàn bộ đáp án thuộc về câu hỏi này
                    java.util.List<org.swp.my_learning_path.entity.QuizAnswer> answers = lessonService.getQuizAnswersByQuestion(q);
                    java.util.List<Map<String, Object>> answersResponse = new java.util.ArrayList<>();
                    for (org.swp.my_learning_path.entity.QuizAnswer a : answers) {
                        Map<String, Object> aMap = new HashMap<>();
                        aMap.put("answerId", a.getAnswerId());
                        aMap.put("answerText", a.getAnswerText());
                        aMap.put("isCorrect", a.getIsCorrect());
                        aMap.put("displayOrder", a.getDisplayOrder());
                        answersResponse.add(aMap);
                    }
                    qMap.put("answers", answersResponse); // Đính kèm mảng đáp án vào câu hỏi
                    questionsResponse.add(qMap); // Nhét câu hỏi vào mảng tổng
                }
                response.put("questions", questionsResponse);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * 8. API nhận Ảnh từ CKEditor (khi soạn thảo bài viết) và ném thẳng lên S3
     */
    @PostMapping("/article-image-upload")
    public ResponseEntity<?> uploadCKEditorImage(@RequestParam("upload") MultipartFile file) {
        try {
            String fileUrl = lessonService.uploadIndependentImage(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl); // Trả về link S3 cho CKEditor hiển thị ngay lập tức
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}