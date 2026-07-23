package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.entity.Certificate;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CertificateService;
import org.swp.my_learning_path.service.CourseService;
import org.swp.my_learning_path.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CertificateService certificateService;
    private final org.swp.my_learning_path.service.TagService tagService;

    @GetMapping("/course/{id}")
    public String courseDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        CourseDetailDTO course = courseService.getCourseDetail(id);
        model.addAttribute("course", course);

        boolean enrolled = false;
        boolean isOwner  = false;
        Certificate certificate = null;

        if (userDetails != null) {
            Long currentUserId = userDetails.getUser().getUserId();
            enrolled = enrollmentService.isEnrolled(currentUserId, id);
            // Giảng viên sở hữu khoá học → ẩn nút đăng ký, hiện nút quản lý
            isOwner  = course.getInstructorId() != null
                    && course.getInstructorId().equals(currentUserId);
            certificate = certificateService.findCertificate(currentUserId, id);
        }
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("isOwner",  isOwner);
        model.addAttribute("certificate", certificate);
        // Tên đầy đủ user hiện tại (dùng để filter "Bài viết của tôi")
        String currentUserName = (userDetails != null && userDetails.getUser().getFullName() != null)
                ? userDetails.getUser().getFullName() : "";
        model.addAttribute("currentUserName", currentUserName);
        return "pages/course-detail";
    }

    @GetMapping({"/instructor/course/{courseId}", "/instructor/course/{courseId}/edit"})
    public String redirectSingularEdit(@PathVariable Long courseId) {
        return "redirect:/instructor/courses/" + courseId + "/edit";
    }

    @GetMapping("/instructor/course/{courseId}/preview")
    public String redirectSingularPreview(@PathVariable Long courseId) {
        return "redirect:/instructor/courses/" + courseId + "/preview";
    }

    @GetMapping("/payment/{courseId}")
    public String paymentPage(@PathVariable Long courseId, Model model) {
        CourseDetailDTO course = courseService.getCourseDetail(courseId);
        model.addAttribute("course", course);
        return "pages/payment";
    }

    @GetMapping("/courses")
    public String courses(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "tag", defaultValue = "") String selectedTag,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long studentId = (userDetails != null) ? userDetails.getUserId() : null;
        List<CourseCardDTO> allCourses = courseService.getCourses(studentId);

        // Lọc theo tag nếu có chọn
        if (selectedTag != null && !selectedTag.trim().isEmpty()) {
            String filterTag = selectedTag.trim().toLowerCase();
            allCourses = allCourses.stream()
                    .filter(c -> {
                        if (c.getTags() != null && !c.getTags().isEmpty()) {
                            return c.getTags().stream().anyMatch(t -> t.toLowerCase().contains(filterTag) || filterTag.contains(t.toLowerCase()));
                        }
                        // Fallback match với tiêu đề khoá học nếu tag chưa được tạo đầy đủ
                        return c.getTitle() != null && c.getTitle().toLowerCase().contains(filterTag);
                    })
                    .toList();
        }

        // Phân trang 10 khoá học / trang (2 hàng x 5 ô)
        int pageSize = 10;
        int totalItems = allCourses.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages < 1) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);
        List<CourseCardDTO> pagedCourses = (fromIndex < totalItems) ? allCourses.subList(fromIndex, toIndex) : List.of();

        // Lấy danh sách tất cả các tag hệ thống
        List<org.swp.my_learning_path.entity.Tag> tags = tagService.getAllTags();

        model.addAttribute("courses", pagedCourses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("selectedTag", selectedTag);
        model.addAttribute("tags", tags);
        return "pages/course-list";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", defaultValue = "") String keyword,
                         @RequestParam(value = "sort", defaultValue = "") String sort,
                         @RequestParam(value = "price", defaultValue = "") String priceRange,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long studentId = (userDetails != null) ? userDetails.getUserId() : null;
        List<CourseCardDTO> results = courseService.searchCourses(keyword, studentId, sort, priceRange);
        model.addAttribute("courses", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("totalResults", results.size());
        return "pages/search-results";
    }

    /**
     * Đăng ký khoá học miễn phí (price = 0) — không qua thanh toán.
     * Trả về JSON để frontend xử lý redirect.
     */
    @PostMapping("/api/enroll/free/{courseId}")
    @ResponseBody
    public ResponseEntity<?> enrollFree(@PathVariable Long courseId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Chưa đăng nhập → yêu cầu login
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("redirect", "/login"));
        }

        try {
            Long studentId = userDetails.getUser().getUserId();
            enrollmentService.enrollFree(studentId, courseId);
            // Thành công → redirect về trang chi tiết khoá học
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "redirect", "/course/" + courseId
            ));
        } catch (IllegalStateException e) {
            // Đã đăng ký rồi hoặc không phải khoá học miễn phí
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}

