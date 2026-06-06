package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.CreateLearningPathRequest;
import org.swp.my_learning_path.entity.LearningPath;
import org.swp.my_learning_path.entity.LearningPathCourse;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.LearningPathCourseRepository;
import org.swp.my_learning_path.repository.LearningPathRepository;
import org.swp.my_learning_path.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.dto.LearningPathDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LearningPathServiceImpl
        implements LearningPathService {

    private final LearningPathRepository learningPathRepository;

    private final LearningPathCourseRepository learningPathCourseRepository;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    @Override
    public List<LearningPathDto> getMyLearningPaths(Long userId, Long courseId) {
        return learningPathRepository
                .findByUser_UserId(userId)
                .stream()
                .map(path -> {
                    boolean selected =
                            learningPathCourseRepository
                                    .existsByLearningPath_PathIdAndCourse_CourseId(
                                            path.getPathId(),
                                            courseId
                                    );

                            return LearningPathDto.builder()
                                    .pathId(path.getPathId())
                                    .title(path.getTitle())
                                    .selected(selected)
                                    .build();
                })
                .toList();
    }

    @Override
    public void addCourseToPath(
            Long pathId,
            Long courseId
    ) {

        if (learningPathCourseRepository
                .existsByLearningPath_PathIdAndCourse_CourseId(
                        pathId,
                        courseId
                )) {

            return;
        }
        Integer displayOrder =
                learningPathCourseRepository
                        .countByLearningPath_PathId(pathId) + 1;
        LearningPathCourse item =
                LearningPathCourse.builder()
                        .learningPath(
                                learningPathRepository
                                        .getReferenceById(pathId)
                        )
                        .course(
                                courseRepository
                                        .getReferenceById(courseId)
                        )
                        .displayOrder(displayOrder)
                        .build();

        learningPathCourseRepository.save(item);
    }

    @Override
    @Transactional
    public void removeCourseFromPath(
            Long pathId,
            Long courseId
    ) {

        LearningPath path =
                learningPathRepository
                        .findById(pathId)
                        .orElseThrow();

        learningPathCourseRepository
                .findByLearningPath_PathIdAndCourse_CourseId(
                        pathId,
                        courseId
                )
                .ifPresent(
                        learningPathCourseRepository::delete
                );
    }

    @Override
    @Transactional
    public LearningPathDto createLearningPath(
            Long userId,
            CreateLearningPathRequest request
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        LearningPath path =
                LearningPath.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .user(user)
                        .build();

        path = learningPathRepository.save(path);

        return LearningPathDto.builder()
                .pathId(path.getPathId())
                .title(path.getTitle())
                .selected(false)
                .build();
    }
}