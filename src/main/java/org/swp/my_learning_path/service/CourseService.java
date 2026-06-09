package org.swp.my_learning_path.service;
import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import java.util.List;
public interface CourseService {
    List<CourseCardDTO> getTop5Courses(Long studentId);
    List<CourseCardDTO> getCourses(Long studentId);
    CourseDetailDTO getCourseDetail(Long courseId); // Hàm mới thêm
}