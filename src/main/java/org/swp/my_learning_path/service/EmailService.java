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
        String statusUrl = "http://localhost:8080/instructor/apply/status";
        
        String content = "";
        if (approved) {
            content = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">"
                    + "    <div style=\"background-color: #10b981; padding: 24px; text-align: center; color: white;\">"
                    + "        <h2 style=\"margin: 0; font-size: 22px; font-weight: 600;\">Chúc Mừng Giảng Viên Mới!</h2>"
                    + "    </div>"
                    + "    <div style=\"padding: 24px; color: #334155; line-height: 1.6;\">"
                    + "        <p style=\"font-size: 16px; margin-top: 0;\">Xin chào <strong>" + fullName + "</strong>,</p>"
                    + "        <p style=\"font-size: 15px;\">Chúng tôi rất vui mừng thông báo rằng hồ sơ ứng tuyển giảng viên của bạn đã được <strong>phê duyệt thành công</strong> trên hệ thống <strong>My Learning Path</strong>!</p>"
                    + "        <p style=\"font-size: 15px;\">Tài khoản của bạn đã được nâng cấp lên vai trò <strong>Giảng viên</strong>. Giờ đây, bạn có thể bắt đầu xây dựng bài giảng, tạo các khóa học chất lượng và chia sẻ kiến thức của mình tới hàng ngàn học viên.</p>"
                    + "        <div style=\"margin: 32px 0; text-align: center;\">"
                    + "            <a href=\"" + statusUrl + "\" style=\"background-color: #10b981; color: white; padding: 12px 28px; text-decoration: none; border-radius: 6px; font-weight: 600; font-size: 15px; display: inline-block; box-shadow: 0 2px 4px rgba(0,0,0,0.1);\">Xem trạng thái hồ sơ của bạn</a>"
                    + "        </div>"
                    + "        <p style=\"font-size: 14px; color: #64748b;\">Nếu nút trên không hoạt động, vui lòng copy đường dẫn dưới đây dán vào trình duyệt:<br>"
                    + "        <a href=\"" + statusUrl + "\" style=\"color: #10b981;\">" + statusUrl + "</a></p>"
                    + "    </div>"
                    + "    <div style=\"background-color: #f8fafc; padding: 16px; text-align: center; font-size: 13px; color: #94a3b8; border-top: 1px solid #e2e8f0;\">"
                    + "        Trân trọng,<br><strong>Đội ngũ Ban quản trị My Learning Path</strong>"
                    + "    </div>"
                    + "</div>";
        } else {
            String note = (reviewNote != null && !reviewNote.trim().isEmpty()) ? reviewNote : "Không có lý do chi tiết";
            content = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">"
                    + "    <div style=\"background-color: #ef4444; padding: 24px; text-align: center; color: white;\">"
                    + "        <h2 style=\"margin: 0; font-size: 22px; font-weight: 600;\">Thông Báo Kết Quả Hồ Sơ</h2>"
                    + "    </div>"
                    + "    <div style=\"padding: 24px; color: #334155; line-height: 1.6;\">"
                    + "        <p style=\"font-size: 16px; margin-top: 0;\">Xin chào <strong>" + fullName + "</strong>,</p>"
                    + "        <p style=\"font-size: 15px;\">Cảm ơn bạn đã quan tâm và nộp hồ sơ ứng tuyển làm giảng viên trên hệ thống <strong>My Learning Path</strong>.</p>"
                    + "        <p style=\"font-size: 15px;\">Sau quá trình xem xét kỹ lưỡng, rất tiếc ban quản trị chưa thể thông qua hồ sơ của bạn vào lúc này.</p>"
                    + "        <div style=\"background-color: #fef2f2; border-left: 4px solid #ef4444; padding: 16px; margin: 20px 0; border-radius: 4px;\">"
                    + "            <strong style=\"color: #991b1b; display: block; margin-bottom: 4px;\">Lý do phản hồi:</strong>"
                    + "            <span style=\"color: #7f1d1d; font-size: 14.5px;\">" + note + "</span>"
                    + "        </div>"
                    + "        <p style=\"font-size: 15px;\">Bạn có thể điều chỉnh lại hồ sơ năng lực của mình theo góp ý trên và nộp lại đơn ứng tuyển bất cứ lúc nào.</p>"
                    + "        <div style=\"margin: 32px 0; text-align: center;\">"
                    + "            <a href=\"" + statusUrl + "\" style=\"background-color: #ef4444; color: white; padding: 12px 28px; text-decoration: none; border-radius: 6px; font-weight: 600; font-size: 15px; display: inline-block; box-shadow: 0 2px 4px rgba(0,0,0,0.1);\">Xem chi tiết & Cập nhật hồ sơ</a>"
                    + "        </div>"
                    + "        <p style=\"font-size: 14px; color: #64748b;\">Nếu nút trên không hoạt động, vui lòng copy đường dẫn dưới đây dán vào trình duyệt:<br>"
                    + "        <a href=\"" + statusUrl + "\" style=\"color: #ef4444;\">" + statusUrl + "</a></p>"
                    + "    </div>"
                    + "    <div style=\"background-color: #f8fafc; padding: 16px; text-align: center; font-size: 13px; color: #94a3b8; border-top: 1px solid #e2e8f0;\">"
                    + "        Trân trọng,<br><strong>Đội ngũ Ban quản trị My Learning Path</strong>"
                    + "    </div>"
                    + "</div>";
        }

        User user = userRepository.findByEmail(toEmail).orElse(null);

        EmailNotification emailNotification = EmailNotification.builder()
                .user(user)
                .recipientEmail(toEmail)
                .subject(subject)
                .content(content)
                .status(EEmailStatus.PENDING)
                .build();

        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                    new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
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
