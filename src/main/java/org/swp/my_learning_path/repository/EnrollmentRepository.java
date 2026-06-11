package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Enrollment;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Lấy danh sách học viên đăng ký khóa học của giảng viên (lọc theo courseId nếu có)
    @Query("SELECT e FROM Enrollment e " +
           "JOIN e.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND e.deleteFlag = false " +
           "AND c.deleteFlag = false " +
           "AND (:courseId IS NULL OR c.courseId = :courseId) " +
           "ORDER BY e.createdAt DESC")
    List<Enrollment> findEnrollmentsByInstructorAndCourse(
            @Param("instructorId") Long instructorId,
            @Param("courseId") Long courseId
    );

    // Kiểm tra học viên đã đăng ký khoá học chưa
    boolean existsByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);

    // Lấy tất cả khoá học đã đăng ký của học viên
    List<Enrollment> findByStudent_UserId(Long studentId);
}
