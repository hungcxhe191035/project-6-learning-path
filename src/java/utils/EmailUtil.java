package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class EmailUtil {

    public static void sendOTP(String toEmail, String otp) {

        ResourceBundle rb = ResourceBundle.getBundle("config.email");

        Properties props = new Properties();
        props.put("mail.smtp.host", rb.getString("mail.smtp.host"));
        props.put("mail.smtp.port", rb.getString("mail.smtp.port"));
        props.put("mail.smtp.auth", rb.getString("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", rb.getString("mail.smtp.starttls.enable"));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        rb.getString("mail.sender.email"),
                        rb.getString("mail.sender.password")
                );
            }
        });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(rb.getString("mail.sender.email")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            message.setSubject("OTP Verification Code - Learning Path Community");

            String htmlContent =
                    "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "  <meta charset='UTF-8'>"
                    + "  <style>"
                    + "    body {"
                    + "      font-family: Arial, sans-serif;"
                    + "      background-color: #f4f6f9;"
                    + "      margin: 0;"
                    + "      padding: 0;"
                    + "    }"
                    + "    .container {"
                    + "      max-width: 1200px;"
                    + "      margin: 40px auto;"
                    + "      background: #ffffff;"
                    + "      border-radius: 12px;"
                    + "      overflow: hidden;"
                    + "      box-shadow: 0 4px 12px rgba(0,0,0,0.1);"
                    + "    }"
                    + "    .header {"
                    + "      background: linear-gradient(135deg, #4f46e5, #6366f1);"
                    + "      color: white;"
                    + "      text-align: center;"
                    + "      padding: 30px 20px;"
                    + "    }"
                    + "    .header h1 {"
                    + "      margin: 0;"
                    + "      font-size: 28px;"
                    + "    }"
                    + "    .content {"
                    + "      font-size: 18px;"
                    + "      padding: 40px 30px;"
                    + "      color: #333333;"
                    + "      line-height: 1.6;"
                    + "    }"
                    + "    .otp-box {"
                    + "      background-color: #eef2ff;"
                    + "      border: 2px dashed #4f46e5;"
                    + "      padding: 20px;"
                    + "      text-align: center;"
                    + "      margin: 30px 0;"
                    + "      border-radius: 10px;"
                    + "    }"
                    + "    .otp-code {"
                    + "      font-size: 36px;"
                    + "      font-weight: bold;"
                    + "      color: #4f46e5;"
                    + "      letter-spacing: 8px;"
                    + "    }"
                    + "    .warning {"
                    + "      color: #dc2626;"
                    + "      font-size: 14px;"
                    + "      margin-top: 20px;"
                    + "    }"
                    + "    .footer {"
                    + "      background-color: #f9fafb;"
                    + "      text-align: center;"
                    + "      padding: 20px;"
                    + "      font-size: 13px;"
                    + "      color: #6b7280;"
                    + "    }"
                    + "  </style>"
                    + "</head>"

                    + "<body>"

                    + "  <div class='container'>"

                    + "    <div class='header'>"
                    + "      <h1>Learning Path Community</h1>"
                    + "      <p>Password Reset Verification</p>"
                    + "    </div>"

                    + "    <div class='content'>"
                    + "      <h2>Hello,</h2>"

                    + "      <p>"
                    + "        We received a request to reset your password."
                    + "        Please use the OTP verification code below:"
                    + "      </p>"

                    + "      <div class='otp-box'>"
                    + "        <div class='otp-code'>" + otp + "</div>"
                    + "      </div>"

                    + "      <p>"
                    + "        This OTP code is valid for <b>5 minutes</b>."
                    + "      </p>"

                    + "      <p class='warning'>"
                    + "        ⚠ Do not share this code with anyone for security reasons."
                    + "      </p>"

                    + "      <p>"
                    + "        If you did not request a password reset, you can safely ignore this email."
                    + "      </p>"
                    + "    </div>"

                    + "    <div class='footer'>"
                    + "      © 2026 Learning Path Community <br>"
                    + "      This is an automated email, please do not reply."
                    + "    </div>"

                    + "  </div>"

                    + "</body>"
                    + "</html>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("OTP email sent successfully to: " + toEmail);

        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

