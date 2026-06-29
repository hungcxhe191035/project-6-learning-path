package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.entity.WalletTransaction;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.SystemSettingService;
import org.swp.my_learning_path.service.WalletService;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final SystemSettingService systemSettingService;

    @GetMapping("/wallet")
    public String myWallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = userDetails.getUserId();
        Wallet wallet = walletService.getWalletByUserId(userId);
        List<WalletTransaction> transactions = walletService.getTransactionHistory(userId);

        model.addAttribute("wallet", wallet);
        model.addAttribute("transactions", transactions);
        return "pages/wallet";
    }

    @GetMapping("/wallet/callback")
    public String vnpayCallback(@RequestParam Map<String, String> allParams, Model model) {
        boolean success = walletService.processVNPayCallback(allParams);
        
        String amountStr = allParams.get("vnp_Amount");
        String orderInfo = allParams.get("vnp_OrderInfo");
        
        double amount = 0;
        if (amountStr != null) {
            amount = Double.parseDouble(amountStr) / 100.0;
        }

        model.addAttribute("paymentSuccess", success);
        model.addAttribute("amount", amount);
        model.addAttribute("orderInfo", orderInfo);
        
        return "pages/wallet-callback";
    }

    @GetMapping("/admin/transactions")
    public String adminTransactions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Cần đảm bảo role là ADMIN
        if (!"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return "redirect:/home";
        }

        List<WalletTransaction> transactions = walletService.getAllTransactions();
        String sharePercent = systemSettingService.getSettingValue("INSTRUCTOR_REVENUE_SHARE_PERCENT", "80");

        model.addAttribute("transactions", transactions);
        model.addAttribute("sharePercent", sharePercent);
        model.addAttribute("activePage", "transactions");
        return "pages/admin/transactions";
    }
}
