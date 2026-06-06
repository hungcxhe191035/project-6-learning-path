package org.swp.my_learning_path.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.dto.request.CreateCourseRequest;
import org.swp.my_learning_path.service.InstructorCourseService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor/courses")
@RequiredArgsConstructor
public class InstructorCourseRestController {

    private final InstructorCourseService instructorCourseService;

    @PostMapping("/draft")
    public ResponseEntity<?> createDraftCourse(@Valid @RequestBody CreateCourseRequest request) {
        try {
            Long newCourseId = instructorCourseService.createDraftCourse(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo khóa học nháp thành công!");
            response.put("courseId", newCourseId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    //Bạn dán API PUT, phase 3, tạo API chỉnh sửa khóa học
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourseInfo(
            @PathVariable Long courseId,
            @RequestBody org.swp.my_learning_path.dto.request.UpdateCourseInfoRequest request) {
        try {
            instructorCourseService.updateCourseInfo(courseId, request);
            return ResponseEntity.ok("Cập nhật thông tin khóa học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // ================== CÁC API MỚI (PHASE 7) ================== //

    @PutMapping("/{courseId}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long courseId) {
        try {
            instructorCourseService.publishCourse(courseId);
            return ResponseEntity.ok("Đã xuất bản khóa học thành công! Học viên đã có thể xem khóa học này.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteDraftCourse(@PathVariable Long courseId) {
        try {
            instructorCourseService.deleteDraftCourse(courseId);
            return ResponseEntity.ok("Xóa khóa học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/thumbnail")
    public ResponseEntity<?> uploadThumbnail(@org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            java.util.Map<String, Object> result = instructorCourseService.uploadCourseThumbnail(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tải ảnh: " + e.getMessage());
        }
    }
}