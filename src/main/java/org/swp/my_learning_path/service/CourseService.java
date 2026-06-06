package org.swp.my_learning_path.service;


import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.entity.Course;

import java.util.List;
public interface CourseService {
    List<CourseCardDTO> getTop5Courses();
    List<Course> getPublishedCourses();
}