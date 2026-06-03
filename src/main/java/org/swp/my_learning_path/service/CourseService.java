package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Course;

import java.util.List;

public interface CourseService {

    List<Course> getPublishedCourses();
    Course getCourseById(Long id);
}