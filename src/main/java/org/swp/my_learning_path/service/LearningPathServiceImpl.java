package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.CreateLearningPathRequest;
import org.swp.my_learning_path.dto.request.UpdateLearningPathRequest;
import org.swp.my_learning_path.dto.response.LearningPathCourseDto;
import org.swp.my_learning_path.dto.response.LearningPathDetailDto;
import org.swp.my_learning_path.entity.Course;
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
import org.swp.my_learning_path.dto.response.LearningPathDto;

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
    public List<LearningPathDto> getMyLearningPathsByCourse(Long userId, Long courseId) {
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

                    List<LearningPathCourse> courses = learningPathCourseRepository
                            .findByLearningPath_PathIdOrderByDisplayOrder(path.getPathId());
                    String thumbnailUrl = null;
                    if (!courses.isEmpty()) {
                        Course firstCourse = courses.get(0).getCourse();
                        if (firstCourse != null && 
                            firstCourse.getCurrentPublishedVersion() != null && 
                            firstCourse.getCurrentPublishedVersion().getThumbnail() != null) {
                                                        if (firstCourse.getCurrentPublishedVersion() != null
                                && firstCourse.getCurrentPublishedVersion().getThumbnail() != null) {

                                thumbnailUrl = firstCourse.getCurrentPublishedVersion()
                                                        .getThumbnail()
                                                        .getFileUrl();
                                } else {
                                        thumbnailUrl = null; // hoặc "/images/default-course.png"
                                }
                        }
                    }

                    return LearningPathDto.builder()
                            .pathId(path.getPathId())
                            .title(path.getTitle())
                            .description(path.getDescription())
                            .selected(selected)
                            .courseCount(courses.size())
                            .thumbnailUrl(thumbnailUrl)
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
    @Override
    public List<LearningPathDto> getMyLearningPaths(Long userId) {
        return learningPathRepository
                .findByUser_UserId(userId)
                .stream()
                .map(path -> {
                    List<LearningPathCourse> courses = learningPathCourseRepository
                            .findByLearningPath_PathIdOrderByDisplayOrder(path.getPathId());
                    String thumbnailUrl = null;
                    if (!courses.isEmpty()) {
                        Course firstCourse = courses.get(0).getCourse();
                        if (firstCourse != null && 
                            firstCourse.getCurrentPublishedVersion() != null && 
                            firstCourse.getCurrentPublishedVersion().getThumbnail() != null) {
                            if (firstCourse.getCurrentPublishedVersion() != null
                                && firstCourse.getCurrentPublishedVersion().getThumbnail() != null) {

                                thumbnailUrl = firstCourse.getCurrentPublishedVersion()
                                                        .getThumbnail()
                                                        .getFileUrl();
                                } else {
                                thumbnailUrl = null; // hoặc "/images/default-course.png"
                                }
                        }
                    }

                    return LearningPathDto.builder()
                            .pathId(path.getPathId())
                            .title(path.getTitle())
                            .description(path.getDescription())
                            .courseCount(courses.size())
                            .thumbnailUrl(thumbnailUrl)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LearningPathDetailDto getLearningPathDetail(
            Long userId,
            Long pathId
    ) {

        LearningPath path =
                learningPathRepository
                        .findByPathIdAndUser_UserId(
                                pathId,
                                userId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Learning path not found"
                                )
                        );

        List<LearningPathCourseDto> courses =
                learningPathCourseRepository
                        .findByLearningPath_PathIdOrderByDisplayOrder(pathId)
                        .stream()
                        .map(item -> {

                            Course course = item.getCourse();

                            return LearningPathCourseDto.builder()
                                    .courseId(course.getCourseId())
                                    .displayOrder(item.getDisplayOrder())
                                    .title(
                                            course
                                                    .getCurrentPublishedVersion()
                                                    .getTitle()
                                    )
                                .thumbnailUrl(
                                        course.getCurrentPublishedVersion().getThumbnail() != null
                                                ? course.getCurrentPublishedVersion().getThumbnail().getFileUrl()
                                                : null
                                        )
                                    .shortDescription(
                                            course
                                                    .getCurrentPublishedVersion()
                                                    .getSubtitle()
                                    )
                                    .averageRating(
                                            course.getAverageRating()
                                    )
                                    .totalStudents(
                                            course.getTotalStudents()
                                    )
                                    .instructorName(
                                            course.getInstructor()
                                                    .getFullName()
                                    )
                                    .build();

                        })
                        .toList();

        return LearningPathDetailDto.builder()
                .pathId(path.getPathId())
                .title(path.getTitle())
                .description(path.getDescription())
                .courses(courses)
                .build();
    }

    @Transactional
    @Override
    public LearningPathDto updateLearningPath(
            Long pathId,
            Long userId,
            UpdateLearningPathRequest request
    ) {

        LearningPath path =
                learningPathRepository
                        .findByPathIdAndUser_UserId(
                                pathId,
                                userId
                        )
                        .orElseThrow();

        path.setTitle(
                request.getTitle()
        );

        path.setDescription(
                request.getDescription()
        );

        learningPathRepository.save(path);
        
        return LearningPathDto.builder()
                .pathId(path.getPathId())
                .title(path.getTitle())
                .description(path.getDescription())
                .build();
    }

    @Transactional
    @Override
    public void deleteLearningPath(
            Long pathId,
            Long userId
    ) {

        LearningPath path =
                learningPathRepository
                        .findByPathIdAndUser_UserId(
                                pathId,
                                userId
                        )
                        .orElseThrow();
        learningPathCourseRepository
                .deleteByLearningPath_PathId(pathId);
        learningPathRepository.delete(path);
    }

}