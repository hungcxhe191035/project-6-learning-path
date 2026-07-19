package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.entity.Notification;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        List<Notification> notifications = notificationService.getNotificationsForUser(userDetails.getUserId());
        List<Map<String, Object>> response = notifications.stream()
                .map(n -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("notificationId", n.getNotificationId());
                    map.put("title", n.getTitle() == null ? "" : n.getTitle());
                    map.put("content", n.getContent() == null ? "" : n.getContent());
                    map.put("isRead", n.getIsRead() != null && n.getIsRead());
                    map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : "");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        long count = notificationService.getUnreadCount(userDetails.getUserId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(userDetails.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.clearAllNotifications(userDetails.getUserId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}
