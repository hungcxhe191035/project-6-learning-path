package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CourseService;
import org.swp.my_learning_path.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        CourseDetailDTO course = courseService.getCourseDetail(id);
        model.addAttribute("course", course);

        boolean enrolled = false;
        if (userDetails != null) {
            enrolled = enrollmentService.isEnrolled(userDetails.getUser().getUserId(), id);
        }
        model.addAttribute("enrolled", enrolled);
        return "pages/course-detail";
    }
    @GetMapping("/payment/{courseId}")
    public String paymentPage(@PathVariable Long courseId, Model model) {
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        return "pages/payment";
    }
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute(
                "courses",
                courseService.getCourses()
        );

        return "pages/course-list";
    }


}
