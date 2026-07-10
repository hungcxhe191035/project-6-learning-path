package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    @PostMapping("/{courseId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long courseId,
            @AuthenticationPrincipal
            CustomUserDetails user
    ) {

        boolean added = cartService.addToCart(
                user.getUserId(),
                courseId
        );

        return ResponseEntity.ok(java.util.Map.of("success", true, "added", added));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long courseId,
            @AuthenticationPrincipal
            CustomUserDetails user
    ) {

        cartService.removeFromCart(
                user.getUserId(),
                courseId
        );

        return ResponseEntity.ok().build();
    }
}