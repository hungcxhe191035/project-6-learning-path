package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.entity.Certificate;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CertificateService;
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
    private final CertificateService certificateService;
    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        CourseDetailDTO course = courseService.getCourseDetail(id);
        model.addAttribute("course", course);

        boolean enrolled = false;
        Certificate certificate = null;
        if (userDetails != null) {
            Long userId = userDetails.getUser().getUserId();
            enrolled = enrollmentService.isEnrolled(userId, id);
            certificate = certificateService.findCertificate(userId, id);
        }
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("certificate", certificate);
        return "pages/course-detail";
    }
    @GetMapping("/payment/{courseId}")
    public String paymentPage(@PathVariable Long courseId, Model model) {
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        return "pages/payment";
    }
    @GetMapping("/courses")
    public String courses(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long studentId = (userDetails != null) ? userDetails.getUserId() : null;
        model.addAttribute(
                "courses",
                courseService.getCourses(studentId)
        );

        return "pages/course-list";
    }


}
