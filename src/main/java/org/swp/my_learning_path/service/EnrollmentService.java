package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    // Lấy danh sách học viên đăng ký của giảng viên (lọc theo courseId nếu có, truyền null để lấy tất cả)
    List<Enrollment> getStudentsByInstructor(Long instructorId, Long courseId);
}
