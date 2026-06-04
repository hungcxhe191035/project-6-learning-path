package org.swp.my_learning_path.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CourseVersion;

@Repository
public interface CourseVersionRepository extends JpaRepository<CourseVersion, Long> {
    //ở phase 3, Hàm này giúp hệ thống tự động sinh ra câu
    // SQL đi tìm đúng phiên bản Khóa học đang ở trạng thái Nháp DRAFT của cái courseId đó
    java.util.Optional<CourseVersion>
    findByCourse_CourseIdAndStatus(Long courseId, org.swp.my_learning_path.constant.ECourseStatus status);
}