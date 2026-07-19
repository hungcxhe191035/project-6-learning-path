package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InstructorApplicationRepository extends JpaRepository<InstructorApplication, Long> {

    // Lấy đơn mới nhất của user
    Optional<InstructorApplication> findTopByUserOrderByCreatedAtDesc(User user);

    // Kiểm tra user có đơn đang PENDING không
    boolean existsByUserAndStatus(User user, EApplicationStatus status);

    // Danh sách đơn theo status (Admin)
    Page<InstructorApplication> findByStatus(EApplicationStatus status, Pageable pageable);

    // Đếm số đơn PENDING (cho badge trên sidebar)
    long countByStatus(EApplicationStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InstructorApplication a SET a.createdAt = :createdAt, a.reviewNote = null WHERE a.applicationId = :id")
    void updateCreatedAtAndResetReviewNote(@Param("id") Long id, @Param("createdAt") LocalDateTime createdAt);
}
