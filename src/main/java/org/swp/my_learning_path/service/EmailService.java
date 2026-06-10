package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EEmailStatus;
import org.swp.my_learning_path.entity.EmailNotification;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.EmailNotificationRepository;
import org.swp.my_learning_path.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class   EmailService {

    private final JavaMailSender mailSender;
    private final EmailNotificationRepository emailNotificationRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void sendNewPasswordEmail(String toEmail, String newPassword) {
        String subject = "Mật khẩu mới - My Learning Path";
        String content = "Xin chào!\n\n"
                + "Mật khẩu mới của bạn là: " + newPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu ngay sau đó.\n\n"
                + "Trân trọng!";

        User user = userRepository.findByEmail(toEmail).orElse(null);

        EmailNotification emailNotification = EmailNotification.builder()
                .user(user)
                .recipientEmail(toEmail)
                .subject(subject)
                .content(content)
                .status(EEmailStatus.PENDING)
                .build();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);

            emailNotification.setStatus(EEmailStatus.SENT);
            emailNotification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            emailNotification.setStatus(EEmailStatus.FAILED);
            emailNotification.setErrorMessage(e.getMessage());
            emailNotification.setSentAt(LocalDateTime.now());
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        } finally {
            emailNotificationRepository.save(emailNotification);
        }
    }
}
