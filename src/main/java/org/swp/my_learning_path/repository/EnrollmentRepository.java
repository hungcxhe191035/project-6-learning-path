package org.swp.my_learning_path.repository;
import org.swp.my_learning_path.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Kiểm tra học viên đã đăng ký khoá học chưa
    boolean existsByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);

    // Lấy tất cả khoá học đã đăng ký của học viên
    List<Enrollment> findByStudent_UserId(Long studentId);
}