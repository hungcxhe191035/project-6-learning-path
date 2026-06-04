package org.swp.my_learning_path.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    // 1. Thêm Bài học vào Chương
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

    // 2. Sửa Bài học
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonRequest request) {
        try {
            lessonService.updateLesson(lessonId, request);
            return ResponseEntity.ok("Cập nhật bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 3. Xóa Bài học
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId) {
        try {
            lessonService.deleteLesson(lessonId);
            return ResponseEntity.ok("Xóa bài học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}