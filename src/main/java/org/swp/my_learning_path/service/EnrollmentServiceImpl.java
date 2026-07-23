package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.Enrollment;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.EnrollmentRepository;
import org.swp.my_learning_path.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public List<Enrollment> getStudentsByInstructor(Long instructorId, Long courseId) {
        return enrollmentRepository.findEnrollmentsByInstructorAndCourse(instructorId, courseId);
    }

    @Override
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, courseId);
    }

    @Override
    @Transactional
    public void enrollFree(Long studentId, Long courseId) {
        // 1. Lấy thông tin khoá học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoá học!"));

        // 2. Giảng viên KHÔNG được tự đăng ký khoá học của chính mình
        if (course.getInstructor() != null
                && course.getInstructor().getUserId().equals(studentId)) {
            throw new IllegalStateException("Bạn là giảng viên của khoá học này, không thể tự đăng ký!");
        }

        // 3. Kiểm tra khoá học phải miễn phí (price = 0)
        BigDecimal price = course.getCurrentPublishedVersion().getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Khoá học này không miễn phí, vui lòng thanh toán!");
        }

        // 4. Kiểm tra học viên chưa đăng ký
        if (enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, courseId)) {
            throw new IllegalStateException("Bạn đã đăng ký khoá học này rồi!");
        }

        // 4. Lấy thông tin học viên
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));

        // 5. Tạo bản ghi Enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        enrollmentRepository.save(enrollment);

        // 6. Cập nhật số lượng học viên của khoá học
        course.setTotalStudents((course.getTotalStudents() != null ? course.getTotalStudents() : 0) + 1);
        courseRepository.save(course);
    }
}

