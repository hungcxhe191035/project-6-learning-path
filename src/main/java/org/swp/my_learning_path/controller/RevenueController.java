package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    public String viewRevenue(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        String role = user.getRole().name();

        // Lấy năm hiện tại để query dữ liệu biểu đồ
        int currentYear = LocalDate.now().getYear();

        model.addAttribute("user", user);
        model.addAttribute("currentYear", currentYear);

        if ("ADMIN".equals(role)) {
            model.addAttribute("totalRevenue", revenueService.getAdminTotalRevenue());
            model.addAttribute("salesHistory", revenueService.getAdminSalesHistory());
            // Truyền dữ liệu 12 tháng thật cho biểu đồ
            model.addAttribute("monthlyRevenue", revenueService.getAdminMonthlyRevenue(currentYear));
            model.addAttribute("isAdmin", true);
            return "pages/revenue";
            
        } else if ("INSTRUCTOR".equals(role)) {
            model.addAttribute("totalRevenue", revenueService.getInstructorTotalRevenue(user.getUserId()));
            model.addAttribute("salesHistory", revenueService.getInstructorSalesHistory(user.getUserId()));
            //Truyền dữ liệu 12 tháng thật cho biểu đồ
            model.addAttribute("monthlyRevenue", revenueService.getInstructorMonthlyRevenue(user.getUserId(), currentYear));
            model.addAttribute("isAdmin", false);
            return "pages/revenue";
            
        } else {
            return "redirect:/";
        }
    }
}
