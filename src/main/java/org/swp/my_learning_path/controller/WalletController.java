package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.constant.ETransactionType;
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
    public String adminTransactions(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String search,
                                    Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        // Cần đảm bảo role là ADMIN
        if (!"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return "redirect:/home";
        }

        ETransactionType transactionType = null;
        if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
            try {
                transactionType = ETransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        ETransactionStatus transactionStatus = null;
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                transactionStatus = ETransactionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<WalletTransaction> transactionPage = walletService.searchTransactions(transactionType, transactionStatus, search, pageable);
        String sharePercent = systemSettingService.getSettingValue("INSTRUCTOR_REVENUE_SHARE_PERCENT", "80");

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("sharePercent", sharePercent);
        model.addAttribute("activePage", "transactions");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("totalElements", transactionPage.getTotalElements());

        // Filter attributes
        model.addAttribute("selectedType", type != null ? type.toUpperCase() : "ALL");
        model.addAttribute("selectedStatus", status != null ? status.toUpperCase() : "ALL");
        model.addAttribute("search", search);

        // Option lists for UI
        model.addAttribute("types", ETransactionType.values());
        model.addAttribute("statuses", ETransactionStatus.values());

        return "pages/admin/transactions";
    }
}
