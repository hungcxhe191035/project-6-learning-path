package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
