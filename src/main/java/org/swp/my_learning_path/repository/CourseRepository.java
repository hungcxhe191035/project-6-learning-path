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

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Course c WHERE c.deleteFlag = false AND c.isBlocked = false AND c.currentPublishedVersion.status = :status ORDER BY c.createdAt DESC")
    List<Course> findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
            @org.springframework.data.repository.query.Param("status") ECourseStatus status
    );

    @org.springframework.data.jpa.repository.Query(
        "SELECT c FROM Course c WHERE c.deleteFlag = false AND c.currentPublishedVersion IS NOT NULL AND " +
        "(:blocked IS NULL OR c.isBlocked = :blocked) AND " +
        "(:status IS NULL OR EXISTS (SELECT cv FROM CourseVersion cv WHERE cv.course = c AND cv.status = :status AND cv.createdAt = (SELECT MAX(cv2.createdAt) FROM CourseVersion cv2 WHERE cv2.course = c))) AND " +
        "(:search IS NULL OR EXISTS (SELECT cv FROM CourseVersion cv WHERE cv.course = c AND LOWER(cv.title) LIKE LOWER(CONCAT('%', :search, '%'))) OR LOWER(c.instructor.fullName) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    org.springframework.data.domain.Page<Course> searchCoursesAdmin(
            @org.springframework.data.repository.query.Param("status") ECourseStatus status,
            @org.springframework.data.repository.query.Param("blocked") Boolean blocked,
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable
    );

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