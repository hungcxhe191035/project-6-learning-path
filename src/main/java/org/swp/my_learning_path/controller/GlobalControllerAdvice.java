package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CartService;
import org.swp.my_learning_path.service.WalletService;

import java.math.BigDecimal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final WalletService walletService;
    private final CartService cartService;

    @ModelAttribute("walletBalance")
    public BigDecimal getWalletBalance(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            try {
                Wallet wallet = walletService.getWalletByUserId(userDetails.getUserId());
                return wallet.getBalance();
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }
        return null;
    }

    @ModelAttribute("cartCount")
    public long getCartCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            try {
                return cartService.getCartCount(userDetails.getUserId());
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
}
