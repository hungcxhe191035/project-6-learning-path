package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.repository.CourseRepository;

import java.util.List;

@Controller
@RequestMapping("/instructor/courses")
@RequiredArgsConstructor
public class InstructorUIController {

    private final CourseRepository courseRepository;

    @GetMapping
    public String showCourseList(Model model) {
        // TẠM THỜI: Vẫn giả vờ là Giảng viên mang ID số 1
        Long mockInstructorId = 1L;

        List<Course> courses = courseRepository.findByInstructor_UserIdOrderByCreatedAtDesc(mockInstructorId);
        model.addAttribute("courses", courses);

        // Trả về file HTML giao diện
        return "pages/instructor/course-list";
    }
}