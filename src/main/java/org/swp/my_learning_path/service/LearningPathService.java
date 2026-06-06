package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.LearningPathDto;
import org.swp.my_learning_path.dto.request.CreateLearningPathRequest;

import java.util.List;

public interface LearningPathService {
    List<LearningPathDto> getMyLearningPaths(Long userId, Long courseId);

    void addCourseToPath(
            Long pathId,
            Long courseId
    );

    void removeCourseFromPath(
            Long pathId,
            Long courseId
    );

    LearningPathDto createLearningPath(
            Long userId,
            CreateLearningPathRequest request
    );
}
