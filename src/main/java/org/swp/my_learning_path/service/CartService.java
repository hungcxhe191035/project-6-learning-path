package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.response.CartItemDto;

import java.util.List;

public interface CartService {

    void addToCart(
            Long userId,
            Long courseId
    );

    void removeFromCart(
            Long userId,
            Long courseId
    );

    List<CartItemDto> getMyCart(
            Long userId
    );

    long getCartCount(
            Long userId
    );
}
