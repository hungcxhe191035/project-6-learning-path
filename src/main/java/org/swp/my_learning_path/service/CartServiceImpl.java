package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.dto.response.CartItemDto;
import org.swp.my_learning_path.entity.CartItem;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.CartItemRepository;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl
        implements CartService {

    private final CartItemRepository cartItemRepository;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    @Override
    public void addToCart(
            Long userId,
            Long courseId
    ) {

        boolean exists =
                cartItemRepository
                        .findByUserUserIdAndCourseCourseId(
                                userId,
                                courseId
                        )
                        .isPresent();

        if (exists) {
            return;
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow();

        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow();

        CartItem item =
                CartItem.builder()
                        .user(user)
                        .course(course)
                        .build();

        cartItemRepository.save(item);
    }

    @Override
    public void removeFromCart(
            Long userId,
            Long courseId
    ) {

        cartItemRepository
                .deleteByUserUserIdAndCourseCourseId(
                        userId,
                        courseId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> getMyCart(
            Long userId
    ) {

        return cartItemRepository
                .findByUserUserId(userId)
                .stream()
                .map(item -> {

                    Course course =
                            item.getCourse();

                    return CartItemDto.builder()
                            .cartItemId(
                                    item.getCartItemId()
                            )
                            .courseId(
                                    course.getCourseId()
                            )
                            .courseTitle(
                                    course
                                            .getCurrentPublishedVersion()
                                            .getTitle()
                            )
                            .thumbnailUrl(
                                    course
                                            .getCurrentPublishedVersion()
                                            .getThumbnail().getFileUrl()
                            )
                            .price(
                                    course
                                            .getCurrentPublishedVersion()
                                            .getPrice()
                            )
                            .instructorName(
                                    course
                                            .getInstructor()
                                            .getFullName()
                            )
                            .averageRating(
                                    course.getAverageRating()
                            )
                            .totalReviews(
                                    course.getTotalReviews()
                            )
                            .build();

                })
                .toList();
    }

    @Override
    public long getCartCount(
            Long userId
    ) {

        return cartItemRepository
                .countByUserUserId(userId);
    }
}
