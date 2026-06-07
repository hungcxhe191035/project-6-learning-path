package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.swp.my_learning_path.dto.request.ChangePasswordRequest;
import org.swp.my_learning_path.dto.request.UpdateProfileRequest;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.entity.UserProfile;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;

    // ==========================================
    // 1. XEM PROFILE
    // ==========================================
    @GetMapping
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        User user = userDetails.getUser();
        UserProfile profile = userService.getProfileByUserId(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);

        return "pages/profile";
    }

    // ==========================================
    // 2. MỞ FORM CHỈNH SỬA PROFILE
    // ==========================================
    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        User user = userDetails.getUser();
        UserProfile profile = userService.getProfileByUserId(user.getUserId());

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName(user.getFullName());
        request.setPhone(user.getPhone());
        request.setBankName(user.getBankName());
        request.setBankCode(user.getBankCode());
        request.setBankAccountNumber(user.getBankAccountNumber());
        request.setBankAccountHolder(user.getBankAccountHolder());

        if (profile != null) {
            request.setBio(profile.getBio());
            request.setHeadline(profile.getHeadline());
            request.setFacebookUrl(profile.getFacebookUrl());
            request.setYoutubeUrl(profile.getYoutubeUrl());
            request.setLinkedinUrl(profile.getLinkedinUrl());
        }

        model.addAttribute("request", request);
        model.addAttribute("user", user);

        return "pages/edit-profile";
    }

    // ==========================================
    // 3. LƯU THÔNG TIN CHỈNH SỬA
    // ==========================================
    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute UpdateProfileRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(userDetails.getUser().getUserId(), request);

            // Cập nhật lại SecurityContext để đồng bộ UI hiển thị tên mới/avatar mới ngay lập tức
            org.springframework.security.core.Authentication currentAuth =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null) {
                User updatedUser = userService.getUserByEmail(userDetails.getUser().getEmail());
                org.springframework.security.core.Authentication newAuth = null;

                if (currentAuth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails newDetails = new CustomUserDetails(updatedUser);
                    newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            newDetails,
                            currentAuth.getCredentials(),
                            newDetails.getAuthorities()
                    );
                }

                if (newAuth != null) {
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    // ==========================================
    // 3.5 CÀI ĐẶT TÀI KHOẢN (SETTINGS)
    // ==========================================
    @GetMapping("/settings")
    public String accountSettings(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "pages/settings";
    }

    // ==========================================
    // 4. MỞ FORM ĐỔI MẬT KHẨU
    // ==========================================
    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("request", new ChangePasswordRequest());
        return "pages/change-password";
    }

    // ==========================================
    // 5. XỬ LÝ ĐỔI MẬT KHẨU
    // ==========================================
    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @ModelAttribute ChangePasswordRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.changePassword(userDetails.getUser().getUserId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/change-password";
        }
    }

    // ==========================================
    // 6. XEM GIỎ HÀNG
    // ==========================================
    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "pages/cart";
    }
}
