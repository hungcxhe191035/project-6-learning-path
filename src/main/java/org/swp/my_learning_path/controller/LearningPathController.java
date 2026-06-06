package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.LearningPathDto;
import org.swp.my_learning_path.dto.request.CreateLearningPathRequest;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping
    public List<LearningPathDto> getMyPaths(@RequestParam Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return learningPathService
                .getMyLearningPaths(userId, courseId);
    }

    @PostMapping("/{pathId}/courses/{courseId}")
    public ResponseEntity<?> addCourse(
            @PathVariable Long pathId,
            @PathVariable Long courseId
    ) {

        learningPathService
                .addCourseToPath(
                        pathId,
                        courseId
                );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pathId}/courses/{courseId}")
    public ResponseEntity<?> removeCourse(
            @PathVariable Long pathId,
            @PathVariable Long courseId
    ) {

        learningPathService.removeCourseFromPath(
                pathId,
                courseId
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public LearningPathDto createLearningPath(
            @RequestBody CreateLearningPathRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return learningPathService.createLearningPath(
                userDetails.getUserId(),
                request
        );
    }
}
