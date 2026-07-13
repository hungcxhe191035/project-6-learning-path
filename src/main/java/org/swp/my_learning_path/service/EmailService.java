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

    @Transactional
    public void sendApplicationResultEmail(String toEmail, String fullName, boolean approved, String reviewNote) {
        String subject = "Kết quả duyệt đơn ứng tuyển Giảng viên - My Learning Path";
        String content = "Xin chào " + fullName + ",\n\n"
                + (approved 
                    ? "Hồ sơ ứng tuyển giảng viên của bạn đã được duyệt thành công! Tài khoản của bạn đã được nâng cấp thành Giảng viên.\n\nChúc bạn có những bài giảng tuyệt vời trên hệ thống!" 
                    : "Hồ sơ ứng tuyển giảng viên của bạn đã bị từ chối.\n"
                      + "Lý do: " + (reviewNote != null ? reviewNote : "Không có lý do chi tiết") + "\n\n"
                      + "Bạn có thể điều chỉnh lại thông tin và nộp lại đơn ứng tuyển.")
                + "\n\nTrân trọng!";

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
        } finally {
            emailNotificationRepository.save(emailNotification);
        }
    }

    @Transactional
    public void sendVoucherPromotionEmail(String toEmail, String studentName, String instructorName, String courseTitle, String voucherCode, double discountValue) {
        String subject = "Món quà đặc biệt từ Giảng viên " + instructorName + " - My Learning Path";
        String content = "Xin chào " + studentName + ",\n\n"
                + "Giảng viên " + instructorName + " vừa tạo một mã giảm giá đặc biệt cho khóa học mới của mình: \"" + courseTitle + "\".\n\n"
                + "Nhập mã giảm giá sau tại trang thanh toán để được giảm ngay " + String.format("%,.0f", discountValue) + " đ:\n"
                + "👉 MÃ GIẢM GIÁ: " + voucherCode + "\n\n"
                + "Hãy nhanh tay đăng ký để tiếp tục con đường chinh phục kiến thức của bạn!\n\n"
                + "Trân trọng,\nBan quản trị My Learning Path.";

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
            System.err.println("Gửi email marketing voucher thất bại: " + e.getMessage());
        } finally {
            emailNotificationRepository.save(emailNotification);
        }
    }
}
