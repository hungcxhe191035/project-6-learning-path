package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.request.CreateCourseRequest;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.CourseVersionRepository;
import org.swp.my_learning_path.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class InstructorCourseService {

    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDraftCourse(CreateCourseRequest request) {
        // TẠM THỜI: Gắn cứng ID = 1 là Giảng viên để test
        // cái này thì tạo 1 nick trong database để test
        Long mockInstructorId = 1L;

        User instructor = userRepository.findById(mockInstructorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Giảng viên!"));

        // 1. Tạo khóa học gốc
        Course newCourse = Course.builder()
                .instructor(instructor)
                .build();
        newCourse = courseRepository.save(newCourse);

        // 2. Tạo phiên bản nháp số 1
        CourseVersion draftVersion = CourseVersion.builder()
                .course(newCourse)
                .title(request.getTitle())
                .versionNumber(1)
                .status(ECourseStatus.DRAFT) // Trạng thái nháp
                .build();
        courseVersionRepository.save(draftVersion);

        return newCourse.getCourseId();
    }
}