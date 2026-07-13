package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.Voucher;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.VoucherService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/instructor/vouchers")
@RequiredArgsConstructor
public class InstructorVoucherController {

    private final VoucherService voucherService;
    private final CourseRepository courseRepository;
    private final org.swp.my_learning_path.repository.UserRepository userRepository;
    private final org.swp.my_learning_path.service.EmailService emailService;
    private final org.swp.my_learning_path.repository.EnrollmentRepository enrollmentRepository;

    @GetMapping
    public String listVouchers(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long instructorId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();

        List<Voucher> vouchers = voucherService.getVouchersByInstructor(instructorId);
        List<Course> courses = courseRepository.findByInstructor_UserIdAndDeleteFlagFalseOrderByCreatedAtDesc(instructorId);

        model.addAttribute("vouchers", vouchers);
        model.addAttribute("courses", courses);
        model.addAttribute("pageTitle", "Quản lý Voucher của tôi");
        return "pages/instructor/vouchers";
    }

    @PostMapping("/create")
    public String createVoucher(
            @RequestParam String code,
            @RequestParam double discountValue,
            @RequestParam double minOrderAmount,
            @RequestParam int limitUsage,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Long courseId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Khóa học không tồn tại!"));

            // Đảm bảo giảng viên chỉ tạo voucher cho khóa học của chính mình
            if (!course.getInstructor().getUserId().equals(userDetails.getUser().getUserId())) {
                throw new IllegalStateException("Bạn không sở hữu khóa học này!");
            }

            Voucher voucher = Voucher.builder()
                    .code(code.toUpperCase().trim())
                    .discountValue(java.math.BigDecimal.valueOf(discountValue))
                    .minOrderAmount(java.math.BigDecimal.valueOf(minOrderAmount))
                    .creatorRole("INSTRUCTOR")
                    .instructor(userDetails.getUser())
                    .course(course)
                    .limitUsage(limitUsage)
                    .startDate(LocalDateTime.parse(startDate))
                    .endDate(LocalDateTime.parse(endDate))
                    .build();

            voucherService.createVoucher(voucher);
            
            // Gửi email marketing chạy ngầm (Asynchronous) tránh lag UI
            new Thread(() -> {
                try {
                    // Lấy danh sách Enrollments của các học viên đã mua khóa học của Giảng viên này
                    java.util.List<org.swp.my_learning_path.entity.Enrollment> enrollments = 
                        enrollmentRepository.findEnrollmentsByInstructorAndCourse(userDetails.getUser().getUserId(), null);
                    
                    String instName = userDetails.getUser().getFullName();
                    String courseTitle = course.getCurrentPublishedVersion() != null 
                        ? course.getCurrentPublishedVersion().getTitle() 
                        : "Khóa học của " + instName;
                    
                    // Lọc trùng học viên để tránh gửi nhiều email cho cùng 1 người nếu họ mua nhiều khóa
                    java.util.Set<String> sentEmails = new java.util.HashSet<>();

                    for (org.swp.my_learning_path.entity.Enrollment enrollment : enrollments) {
                        org.swp.my_learning_path.entity.User student = enrollment.getStudent();
                        if (student != null && student.getEmail() != null && !sentEmails.contains(student.getEmail())) {
                            sentEmails.add(student.getEmail());
                            emailService.sendVoucherPromotionEmail(
                                student.getEmail(),
                                student.getFullName(),
                                instName,
                                courseTitle,
                                voucher.getCode(),
                                discountValue
                            );
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Lỗi gửi email quảng cáo voucher: " + ex.getMessage());
                }
            }).start();

            redirectAttributes.addFlashAttribute("successMessage", "Tạo mã giảm giá thành công! Email quảng bá đã được gửi tới học viên.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tạo voucher: " + e.getMessage());
        }
        return "redirect:/instructor/vouchers";
    }

    @PostMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xóa voucher: " + e.getMessage());
        }
        return "redirect:/instructor/vouchers";
    }
}
