package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.request.SubmitApplicationRequest;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.InstructorApplicationService;
import org.swp.my_learning_path.service.TagService;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.constant.EApplicationStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorApplicationController {

    private final InstructorApplicationService applicationService;
    private final TagService tagService;

    // =============================================
    // FORM NỘP ĐƠN XIN TRỞ THÀNH GIẢNG VIÊN
    // =============================================
    @GetMapping("/apply")
    public String applyForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "resubmit", required = false) Boolean resubmit,
            Model model) {
        // Nếu đã là INSTRUCTOR → chuyển đến trang status
        if (userDetails.getUser().getRole() == ERole.INSTRUCTOR) {
            return "redirect:/instructor/apply/status";
        }

        // Kiểm tra đơn hiện tại
        Optional<InstructorApplication> latestApp =
                applicationService.getMyLatestApplication(userDetails.getUser().getUserId());

        // Nếu có đơn đang PENDING: chuyển hướng nếu chưa quá 30 giây HOẶC (đã quá 30 giây nhưng không chủ động bấm gửi lại)
        if (latestApp.isPresent() &&
                latestApp.get().getStatus() == EApplicationStatus.PENDING) {
            LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
            if (latestApp.get().getCreatedAt().isAfter(tenDaysAgo) || !Boolean.TRUE.equals(resubmit)) {
                return "redirect:/instructor/apply/status";
            }
        }

        // Nếu đơn trước REJECTED hoặc PENDING (đã quá 10 ngày) và có CV → hiển thị thông tin cũ để người dùng sửa đổi
        String existingCvName = null;
        SubmitApplicationRequest form = new SubmitApplicationRequest();
        if (latestApp.isPresent()) {
            InstructorApplication app = latestApp.get();
            if (app.getStatus() == EApplicationStatus.REJECTED || app.getStatus() == EApplicationStatus.PENDING) {
                form.setHeadline(app.getHeadline());
                form.setBio(app.getBio());
                form.setMotivation(app.getMotivation());
                form.setLinkedinUrl(app.getLinkedinUrl());
                if (app.getTeachingTags() != null) {
                    List<Long> tagIds = app.getTeachingTags().stream()
                            .map(Tag::getTagId)
                            .toList();
                    form.setTagIds(tagIds);
                }
            }
            if (app.getCvFileName() != null) {
                existingCvName = app.getCvFileName();
            }
        }

        model.addAttribute("applicationForm", form);
        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("existingCvName", existingCvName);
        model.addAttribute("pageTitle", "Đăng ký trở thành Giảng viên");
        return "pages/instructor/apply";
    }

    @PostMapping(value = "/apply", consumes = "multipart/form-data")
    public String submitApply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("applicationForm") SubmitApplicationRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (userDetails.getUser().getRole() == ERole.INSTRUCTOR) {
            return "redirect:/instructor/apply/status";
        }

        if (bindingResult.hasErrors()) {
            populateExistingCvName(userDetails, model);
            model.addAttribute("allTags", tagService.getAllTags());
            model.addAttribute("pageTitle", "Đăng ký trở thành Giảng viên");
            return "pages/instructor/apply";
        }

        // Validate CV file nếu có upload
        if (cvFile != null && !cvFile.isEmpty()) {
            String originalName = cvFile.getOriginalFilename();
            if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
                populateExistingCvName(userDetails, model);
                model.addAttribute("error", "Chỉ chấp nhận file PDF cho CV.");
                model.addAttribute("allTags", tagService.getAllTags());
                model.addAttribute("pageTitle", "Đăng ký trở thành Giảng viên");
                return "pages/instructor/apply";
            }
            long maxSizeBytes = 10L * 1024 * 1024; // 10MB
            if (cvFile.getSize() > maxSizeBytes) {
                populateExistingCvName(userDetails, model);
                model.addAttribute("error", "File CV không được vượt quá 10MB.");
                model.addAttribute("allTags", tagService.getAllTags());
                model.addAttribute("pageTitle", "Đăng ký trở thành Giảng viên");
                return "pages/instructor/apply";
            }
        }

        try {
            applicationService.submitApplication(
                    userDetails.getUser().getUserId(), request, cvFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đơn đăng ký của bạn đã được gửi thành công! Chúng tôi sẽ xem xét và phản hồi sớm nhất.");
            return "redirect:/instructor/apply/status";
        } catch (RuntimeException e) {
            populateExistingCvName(userDetails, model);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allTags", tagService.getAllTags());
            model.addAttribute("pageTitle", "Đăng ký trở thành Giảng viên");
            return "pages/instructor/apply";
        }
    }

    private void populateExistingCvName(CustomUserDetails userDetails, Model model) {
        Optional<InstructorApplication> latestApp =
                applicationService.getMyLatestApplication(userDetails.getUser().getUserId());
        String existingCvName = null;
        if (latestApp.isPresent() && latestApp.get().getCvFileName() != null) {
            existingCvName = latestApp.get().getCvFileName();
        }
        model.addAttribute("existingCvName", existingCvName);
    }

    // =============================================
    // TRANG TRẠNG THÁI ĐƠN
    // =============================================
    @GetMapping("/apply/status")
    public String applyStatus(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Optional<InstructorApplication> latestApp =
                applicationService.getMyLatestApplication(userDetails.getUser().getUserId());

        boolean canResubmit = false;
        if (latestApp.isPresent()) {
            InstructorApplication app = latestApp.get();
            if (app.getStatus() == EApplicationStatus.REJECTED) {
                canResubmit = true;
            } else if (app.getStatus() == EApplicationStatus.PENDING) {
                LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
                if (!app.getCreatedAt().isAfter(tenDaysAgo)) {
                    canResubmit = true;
                }
            }
        }

        model.addAttribute("instructorApp", latestApp.orElse(null));
        model.addAttribute("canResubmit", canResubmit);
        model.addAttribute("currentUser", userDetails.getUser());
        model.addAttribute("pageTitle", "Trạng thái đơn đăng ký Giảng viên");
        return "pages/instructor/apply-status";
    }
}
