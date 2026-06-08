package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.entity.LearningPathCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathCourseRepository
        extends JpaRepository<LearningPathCourse, Long> {

    boolean existsByLearningPath_PathIdAndCourse_CourseId(
            Long pathId,
            Long courseId
    );
    Integer countByLearningPath_PathId(Long pathId);

    Optional<LearningPathCourse> findByLearningPath_PathIdAndCourse_CourseId(
            Long pathId,
            Long courseId
    );

    List<LearningPathCourse> findByLearningPath_PathIdOrderByDisplayOrder(
            Long pathId
    );

    void deleteByLearningPath_PathId(Long pathId);
}
