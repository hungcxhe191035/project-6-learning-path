package org.swp.my_learning_path.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập");
        return "pages/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Quên mật khẩu");
        return "pages/forgot-password";
    }

    @GetMapping("/403")
    public String accessDeniedPage(Model model) {
        model.addAttribute("pageTitle", "403 - Từ chối truy cập");
        return "pages/403";
    }
}