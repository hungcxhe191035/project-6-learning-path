package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.service.AdminService;
import org.swp.my_learning_path.service.CourseService;
import org.swp.my_learning_path.service.InstructorApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final AdminService adminService;
    private final CourseService courseService;
    private final InstructorApplicationService applicationService;

    // =============================================
    // DANH SÁCH KHÓA HỌC (Search + Filter + Page)
    // =============================================
    @GetMapping
    public String listCourses(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String statusStr,
            @RequestParam(value = "blocked", required = false) Boolean blocked,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        ECourseStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = ECourseStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bỏ qua giá trị status không hợp lệ
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("courseId").descending());
        Page<Course> coursePage = adminService.searchCourses(status, blocked, search, pageable);

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", statusStr != null ? statusStr.toUpperCase() : "ALL");
        model.addAttribute("selectedBlocked", blocked);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalElements", coursePage.getTotalElements());
        model.addAttribute("statuses", ECourseStatus.values());
        
        model.addAttribute("pageTitle", "Quản lý Khóa học");
        model.addAttribute("activePage", "courses");
        model.addAttribute("pendingCount", applicationService.countPending());

        return "pages/admin/courses";
    }

    // =============================================
    // CHI TIẾT KHÓA HỌC (Read-only)
    // =============================================
    @GetMapping("/{id}")
    public String viewCourseDetail(@PathVariable("id") Long id, Model model) {
        CourseDetailDTO course = courseService.getCourseDetail(id);
        
        model.addAttribute("course", course);
        model.addAttribute("pageTitle", "Chi tiết Khóa học - " + course.getTitle());
        model.addAttribute("activePage", "courses");
        model.addAttribute("pendingCount", applicationService.countPending());

        return "pages/admin/course-detail";
    }

    // =============================================
    // KHÓA KHÓA HỌC (Block)
    // =============================================
    @PostMapping("/{id}/block")
    public String blockCourse(
            @PathVariable("id") Long id,
            @RequestParam("reason") String reason,
            @RequestParam(value = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.blockCourse(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã khóa khóa học thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/courses?page=" + page;
    }

    // =============================================
    // MỞ KHÓA KHÓA HỌC (Unblock)
    // =============================================
    @PostMapping("/{id}/unblock")
    public String unblockCourse(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.unblockCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở khóa khóa học thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/courses?page=" + page;
    }

    // =============================================
    // XÓA MỀM KHÓA HỌC (Delete)
    // =============================================
    @PostMapping("/{id}/delete")
    public String deleteCourse(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khóa học thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/courses?page=" + page;
    }
}
