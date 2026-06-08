package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swp.my_learning_path.entity.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy danh sách thông báo của 1 user, xếp theo thời gian mới nhất
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    
    // Đếm số thông báo CHƯA đọc để hiển thị badge số đỏ trên quả chuông 🔔
    long countByUserUserIdAndIsReadFalse(Long userId);
}
