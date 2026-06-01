package controller;

import com.hsf302.final_project.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập");
        return "pages/login";
    }

    // ───── QUÊN MẬT KHẨU ─────

    /** Hiển thị form nhập email */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Quên mật khẩu");
        return "pages/forgot-password";
    }

    /** Xử lý khi user submit email */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            Model model
    ) {
        try {
            // Tạo base URL động (vd: http://localhost:8080)
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();
            authService.forgotPassword(email, baseUrl);
            model.addAttribute("success", true);
        } catch (RuntimeException e) {
            model.addAttribute("error", true);
        }
        return "pages/forgot-password";
    }

    // ───── ĐẶT LẠI MẬT KHẨU ─────

    /** Hiển thị form đặt mật khẩu mới (từ link email) */
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam("token") String token,
            Model model
    ) {
        model.addAttribute("token", token);
        model.addAttribute("pageTitle", "Đặt lại mật khẩu");
        return "pages/reset-password";
    }

    /** Xử lý khi user submit mật khẩu mới */
    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        // Kiểm tra 2 mật khẩu khớp nhau
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "pages/reset-password";
        }
        try {
            authService.resetPassword(token, newPassword);
            return "redirect:/login?resetSuccess=true";
        } catch (RuntimeException e) {
            model.addAttribute("token", token);
            model.addAttribute("error", e.getMessage());
            return "pages/reset-password";
        }
    }
}
