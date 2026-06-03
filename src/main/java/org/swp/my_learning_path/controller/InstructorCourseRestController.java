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
}