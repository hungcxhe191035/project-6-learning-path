package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserUserId(Long userId);

    Optional<CartItem> findByUserUserIdAndCourseCourseId(
            Long userId,
            Long courseId
    );

    void deleteByUserUserIdAndCourseCourseId(
            Long userId,
            Long courseId
    );

    long countByUserUserId(Long userId);
}