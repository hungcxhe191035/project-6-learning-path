package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.Notification;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void sendNotification(User user, String title, String content) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserUserIdAndIsReadFalse(userId);
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void clearAllNotifications(Long userId) {
        List<Notification> all = notificationRepository.findByUserUserId(userId);
        notificationRepository.deleteAll(all);
    }
}
