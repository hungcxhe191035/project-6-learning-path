package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.Enrollment;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.EnrollmentService;

import java.util.List;

@Controller
@RequestMapping("/instructor/students")
@RequiredArgsConstructor
public class InstructorStudentController {

    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;

    @GetMapping
    public String viewStudents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "courseId", required = false) Long courseId,
            Model model) {

        // Nếu chưa đăng nhập thì chuyển về trang login
        if (userDetails == null) {
            return "redirect:/login";
        }

        User instructor = userDetails.getUser();
        Long instructorId = instructor.getUserId();

        // 1. Lấy toàn bộ khóa học của giảng viên này để hiển thị bộ lọc dropdown
        List<Course> courses = courseRepository
                .findByInstructor_UserIdAndDeleteFlagFalseOrderByCreatedAtDesc(instructorId);

        // 2. Lấy danh sách học viên đăng ký (lọc theo khóa học nếu có chọn)
        List<Enrollment> enrollments = enrollmentService.getStudentsByInstructor(instructorId, courseId);

        // 3. Đẩy dữ liệu ra giao diện Thymeleaf
        model.addAttribute("courses", courses);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("totalStudentsCount", enrollments.size());
        model.addAttribute("user", instructor);

        return "pages/instructor/students";
    }
}
