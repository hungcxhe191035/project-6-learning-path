package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.UpdateLearningPathRequest;
import org.swp.my_learning_path.dto.response.LearningPathDto;
import org.swp.my_learning_path.dto.request.CreateLearningPathRequest;
import org.swp.my_learning_path.dto.response.LearningPathDetailDto;

import java.util.List;

public interface LearningPathService {
    List<LearningPathDto> getMyLearningPathsByCourse(Long userId, Long courseId);

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

    List<LearningPathDto> getMyLearningPaths(Long userId);

    LearningPathDetailDto getLearningPathDetail(
            Long userId,
            Long pathId
    );

    LearningPathDto updateLearningPath(
            Long pathId,
            Long userId,
            UpdateLearningPathRequest request
    );

    void deleteLearningPath(
            Long pathId,
            Long courseId
    );
}
