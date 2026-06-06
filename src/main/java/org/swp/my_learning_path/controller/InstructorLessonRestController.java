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

// phase 3 cụm 3
@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorLessonRestController {

    private final InstructorLessonService lessonService;

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

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonRequest request) {
        try {
            lessonService.updateLesson(lessonId, request);
            return ResponseEntity.ok("Cập nhật bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

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

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<?> getLessonDetail(@PathVariable Long lessonId) {
        try {
            org.swp.my_learning_path.entity.Lesson lesson = lessonService.getLessonById(lessonId);
            Map<String, Object> response = new HashMap<>();
            response.put("lessonId", lesson.getLessonId());
            response.put("lessonType", lesson.getLessonType());
            response.put("articleContent", lesson.getArticleContent());
            if(lesson.getVideo() != null) {
                response.put("videoUrl", lesson.getVideo().getFileUrl());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 7. API nhận Ảnh từ CKEditor và ném thẳng lên S3
    @PostMapping("/article-image-upload")
    public ResponseEntity<?> uploadCKEditorImage(@RequestParam("upload") MultipartFile file) {
        try {
            String fileUrl = lessonService.uploadIndependentImage(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl); // Trả về link S3 cho CKEditor
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi tải ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}