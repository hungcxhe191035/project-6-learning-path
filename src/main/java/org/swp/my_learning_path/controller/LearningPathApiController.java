package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.request.UpdateLearningPathRequest;
import org.swp.my_learning_path.dto.response.LearningPathDto;
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
public class LearningPathApiController {

    private final LearningPathService learningPathService;

    @GetMapping
    public List<LearningPathDto> getMyPaths(@RequestParam Long courseId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return learningPathService
                .getMyLearningPathsByCourse(userId, courseId);
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

    @PutMapping("/{pathId}")
    public LearningPathDto updateLearningPath(
            @PathVariable Long pathId,
            @RequestBody UpdateLearningPathRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return learningPathService.updateLearningPath(
                pathId,
                user.getUserId(),
                request
        );
    }

    @DeleteMapping("/{pathId}")
    public ResponseEntity<Void> deleteLearningPath(
            @PathVariable Long pathId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        learningPathService.deleteLearningPath(
                pathId,
                user.getUserId()
        );

        return ResponseEntity.ok().build();
    }
}
