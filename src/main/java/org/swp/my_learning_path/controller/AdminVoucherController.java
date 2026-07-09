package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.swp.my_learning_path.entity.Voucher;
import org.swp.my_learning_path.service.VoucherService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public String listVouchers(Model model) {
        List<Voucher> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("pageTitle", "Quản lý Voucher - Admin");
        model.addAttribute("activePage", "vouchers");
        return "pages/admin/vouchers";
    }

    @PostMapping("/create")
    public String createVoucher(
            @RequestParam String code,
            @RequestParam double discountValue,
            @RequestParam double minOrderAmount,
            @RequestParam int limitUsage,
            @RequestParam String startDate,
            @RequestParam String endDate,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Voucher voucher = Voucher.builder()
                    .code(code.toUpperCase().trim())
                    .discountValue(java.math.BigDecimal.valueOf(discountValue))
                    .minOrderAmount(java.math.BigDecimal.valueOf(minOrderAmount))
                    .creatorRole("ADMIN")
                    .limitUsage(limitUsage)
                    .startDate(LocalDateTime.parse(startDate))
                    .endDate(LocalDateTime.parse(endDate))
                    .build();

            voucherService.createVoucher(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tạo voucher: " + e.getMessage());
        }
        return "redirect:/admin/vouchers";
    }

    @PostMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa mã giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xóa voucher: " + e.getMessage());
        }
        return "redirect:/admin/vouchers";
    }
}
