package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CourseService;
import org.swp.my_learning_path.service.EnrollmentService;

@Controller
@RequiredArgsConstructor
public class LearningController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    /**
     * Trang học: /learn/{courseId}
     * Chỉ học viên đã đăng ký mới được truy cập.
     */
    @GetMapping("/learn/{courseId}")
    public String learnPage(@PathVariable Long courseId,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        // Chưa đăng nhập → về trang login
        if (userDetails == null) {
            return "redirect:/login";
        }

        Long studentId = userDetails.getUser().getUserId();

        // Chưa đăng ký khoá học → về trang chi tiết
        if (!enrollmentService.isEnrolled(studentId, courseId)) {
            return "redirect:/course/" + courseId;
        }

        // Lấy thông tin khoá học (bao gồm sections & lessons)
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        model.addAttribute("user", userDetails.getUser());

        // Mặc định chọn bài học đầu tiên (nếu có)
        if (course.getSections() != null && !course.getSections().isEmpty()
                && course.getSections().get(0).getLessons() != null
                && !course.getSections().get(0).getLessons().isEmpty()) {
            model.addAttribute("firstLessonId",
                    course.getSections().get(0).getLessons().get(0).getLessonId());
        }

        return "pages/learn";
    }
}
