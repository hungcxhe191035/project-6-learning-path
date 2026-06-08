package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.swp.my_learning_path.dto.request.RegisterRequest;
import org.swp.my_learning_path.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập");
        return "pages/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Đăng ký");
        model.addAttribute("request", new RegisterRequest());
        return "pages/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("request") RegisterRequest request,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Đăng ký");
            return "pages/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Quên mật khẩu");
        return "pages/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            authService.forgotPassword(email);
            model.addAttribute("success", true);
        } catch (RuntimeException e) {
            model.addAttribute("error", true);
        }
        return "pages/forgot-password";
    }

    @GetMapping("/403")
    public String accessDeniedPage(Model model) {
        model.addAttribute("pageTitle", "403 - Từ chối truy cập");
        return "pages/403";
    }
}
