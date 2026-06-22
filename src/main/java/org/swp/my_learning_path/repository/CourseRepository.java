package org.swp.my_learning_path.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.entity.Course;

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
    //thuộc phase 4 về phần giao diện (Đã sửa ở phase 7 để lọc khóa học đã xóa)
    java.util.List<org.swp.my_learning_path.entity.Course>
    findByInstructor_UserIdAndDeleteFlagFalseOrderByCreatedAtDesc(Long instructorId);

    // Tìm kiếm khoá học theo từ khoá (title hoặc subtitle), chỉ lấy APPROVED và chưa bị xóa
    @Query("SELECT c FROM Course c WHERE c.deleteFlag = false " +
           "AND c.currentPublishedVersion.status = :status " +
           "AND (LOWER(c.currentPublishedVersion.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.currentPublishedVersion.subtitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.instructor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<Course> searchByKeyword(@Param("keyword") String keyword,
                                 @Param("status") ECourseStatus status);
}