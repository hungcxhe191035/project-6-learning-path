package org.swp.my_learning_path.service;
import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import java.util.List;
public interface CourseService {
    List<CourseCardDTO> getTop5Courses();
    List<CourseCardDTO> getCourses();
    CourseDetailDTO getCourseDetail(Long courseId); // Hàm mới thêm
}