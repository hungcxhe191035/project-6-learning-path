package org.swp.my_learning_path.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CourseVersion;

@Repository
public interface CourseVersionRepository extends JpaRepository<CourseVersion, Long> {
    // Lấy phiên bản mới nhất của Khóa học (DRAFT hoặc APPROVED)
    java.util.Optional<CourseVersion>
    findFirstByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);

    java.util.Optional<CourseVersion>
    findByCourse_CourseIdAndStatus(Long courseId, org.swp.my_learning_path.constant.ECourseStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(cv) FROM CourseVersion cv WHERE cv.status = :status AND cv.course.deleteFlag = false")
    long countByStatusAndCourseDeleteFlagFalse(@org.springframework.data.repository.query.Param("status") org.swp.my_learning_path.constant.ECourseStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT cv FROM CourseVersion cv WHERE cv.status = :status AND cv.course.deleteFlag = false ORDER BY cv.createdAt DESC")
    java.util.List<CourseVersion> findLatestPendingCourseVersions(@org.springframework.data.repository.query.Param("status") org.swp.my_learning_path.constant.ECourseStatus status, org.springframework.data.domain.Pageable pageable);
}