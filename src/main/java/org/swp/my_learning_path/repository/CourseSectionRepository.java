package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CourseSection;
import java.util.List;
// thuộc tiến độ cụm 2 phase 3 liên quan đến tạo các sess
@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    // Hàm này giúp lấy danh sách Chương và tự động sắp xếp theo thứ tự 1,2,3...
    List<CourseSection> findByCourseVersion_CourseVersionIdOrderByDisplayOrderAsc(Long versionId);
}