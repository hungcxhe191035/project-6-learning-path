package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public String courses(Model model) {

        model.addAttribute(
                "courses",
                courseService.getPublishedCourses()
        );

        return "pages/course-list";
    }
}
