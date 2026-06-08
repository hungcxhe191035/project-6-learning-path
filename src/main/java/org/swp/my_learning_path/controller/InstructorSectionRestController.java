package org.swp.my_learning_path.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.dto.request.SectionRequest;
import org.swp.my_learning_path.service.InstructorSectionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorSectionRestController {

    private final InstructorSectionService sectionService;

    // 1. Thêm Chương mới vào Khóa học
    @PostMapping("/courses/{courseId}/sections")
    public ResponseEntity<?> createSection(@PathVariable Long courseId, @Valid @RequestBody SectionRequest request) {
        try {
            Long sectionId = sectionService.createSection(courseId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo chương thành công!");
            response.put("sectionId", sectionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 2. Sửa tên Chương
    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<?> updateSection(@PathVariable Long sectionId, @Valid @RequestBody SectionRequest request) {
        try {
            sectionService.updateSection(sectionId, request);
            return ResponseEntity.ok("Cập nhật chương thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 3. Xóa Chương
    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable Long sectionId) {
        try {
            sectionService.deleteSection(sectionId);
            return ResponseEntity.ok("Xóa chương thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}