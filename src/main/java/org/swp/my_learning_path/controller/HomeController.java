package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.service.CourseService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CourseService courseService;
    @GetMapping({"/", "/home"})
    public String homePage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Lấy username (email) của người đang login
        String username = (userDetails != null) ? userDetails.getUsername() : "Khách";
        
        Long studentId = null;
        if (userDetails instanceof org.swp.my_learning_path.security.CustomUserDetails cud) {
            studentId = cud.getUserId();
        }
        
        model.addAttribute("username", username);
        model.addAttribute("pageTitle", "Trang chủ - My Learning Path");
        // Lấy danh sách 5 khoá học (Lĩnh vực đề xuất)
        List<CourseCardDTO> recommendedCourses = courseService.getTop5Courses(studentId);
        model.addAttribute("recommendedCourses", recommendedCourses);

        // Lấy danh sách 5 khoá học bán chạy nhất
        List<CourseCardDTO> bestSellingCourses = courseService.getTop5BestSellingCourses(studentId);
        model.addAttribute("bestSellingCourses", bestSellingCourses);
        return "pages/home"; // → templates/pages/home.html
    }
}