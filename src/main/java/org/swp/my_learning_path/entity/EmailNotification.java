package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.swp.my_learning_path.constant.EEmailStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_notification_id")
    Long emailNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "recipient_email",
            nullable = false,
            length = 255)
    String recipientEmail;

    @Column(name = "subject",
            nullable = false,
            columnDefinition = "NVARCHAR(255)")
    String subject;

    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "NVARCHAR(MAX)"
    )
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    EEmailStatus status;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @Column(
            name = "error_message",
            columnDefinition = "NVARCHAR(MAX)"
    )
    String errorMessage;
}