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
                return new PasswordAuthentication(rb.getString("mail.sender.email"), rb.getString("mail.sender.password"));
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(rb.getString("mail.sender.email")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            
            // English Subject and Body
            message.setSubject("OTP Verification Code - Learning Path Community");
            message.setText("Hello,\n\nYour OTP code for password reset is: " + otp + "\n\nThis code is valid for 5 minutes. Please do not share this code with anyone.");
            
            Transport.send(message);
            System.out.println("OTP email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}