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
}