package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Notification;
import org.swp.my_learning_path.entity.User;
import java.util.List;

public interface NotificationService {
    // Hàm dùng chung để tạo thông báo mới
    void sendNotification(User user, String title, String content);

    // Lấy danh sách thông báo của user
    List<Notification> getNotificationsForUser(Long userId);

    // Đếm thông báo chưa đọc
    long getUnreadCount(Long userId);

    // Đánh dấu thông báo đã đọc
    void markAsRead(Long notificationId);
}
