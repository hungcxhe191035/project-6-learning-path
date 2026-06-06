package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CartService;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String cart(
            @AuthenticationPrincipal
            CustomUserDetails user,
            Model model
    ) {

        model.addAttribute(
                "cartItems",
                cartService.getMyCart(
                        user.getUserId()
                )
        );

        return "pages/cart";
    }
}
