package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CartService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;

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
