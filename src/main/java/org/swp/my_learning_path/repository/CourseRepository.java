package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // Dùng method naming của Spring Data JPA
    // Spring tự hiểu: tìm Course mà deleteFlag = false
    // và currentPublishedVersion.status = status
    // sắp xếp theo createdAt giảm dần
    List<Course> findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
            ECourseStatus status
    );
    // Dành cho Giảng viên: Lấy tất cả khóa học của chính họ (sắp xếp mới nhất lên đầu)
    //thuộc phase 4 về phần giao diện
    java.util.List<org.swp.my_learning_path.entity.Course>
    findByInstructor_UserIdOrderByCreatedAtDesc(Long instructorId);

    @Query("""
        SELECT c
        FROM Course c
        JOIN FETCH c.currentPublishedVersion cpv
        JOIN FETCH c.instructor i
        WHERE c.currentPublishedVersion IS NOT NULL
    """)
    List<Course> findAllPublishedCourses();
}