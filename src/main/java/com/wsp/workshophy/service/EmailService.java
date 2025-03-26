package com.wsp.workshophy.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {
    JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thiết lập thông tin email
        helper.setTo(toEmail);
        helper.setSubject("Workshophy - Xác Thực Tài Khoản");
        helper.setFrom("support@workshophy.com");

        // Nội dung email HTML với mã xác thực
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<h2>Chào Mừng Bạn Đến Với Workshophy!</h2>" +
                "<p>Xin chào " + (fullName != null && !fullName.isEmpty() ? fullName : "bạn") + ",</p>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại Workshophy! Để hoàn tất quá trình đăng ký, bạn hãy sử dụng mã xác thực email dưới đây:</p>" +
                "<h3 style='color: #4285F4;'>Mã xác thực: <strong>" + verificationToken + "</strong></h3>" +
                "<p>Mã này sẽ hết hạn sau 24 giờ. Vui lòng nhập mã này vào ứng dụng để xác thực email của bạn.</p>" +
                "<p>Nếu bạn không thực hiện đăng ký này, xin vui lòng bỏ qua email này hoặc liên hệ với chúng mình qua <a href='mailto:support@workshophy.com'>support@workshophy.com</a>.</p>" +
                "<p>Chúng mình rất mong được đồng hành cùng bạn tại Workshophy!</p>" +
                "<p>Thân ái,<br><strong>Đội ngũ Workshophy</strong></p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true); // true: gửi dưới dạng HTML

        // Gửi email
        mailSender.send(message);
    }

    public void sendWelcomeEmailForCustomer(String toEmail, String fullName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Chào Mừng Bạn Đến Với Workshophy!");
        helper.setFrom("support@workshophy.com");

        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<h2>Chào Mừng Khách Hàng Mới!</h2>" +
                "<p>Xin chào " + (fullName != null && !fullName.isEmpty() ? fullName : "bạn") + ",</p>" +
                "<p>Chúng mình rất vui mừng chào đón bạn đến với cộng đồng Workshophy! Tài khoản của bạn đã được xác thực thành công, và giờ đây bạn có thể bắt đầu khám phá các tính năng tuyệt vời của chúng mình.</p>" +
                "<p><strong>Bắt đầu ngay:</strong></p>" +
                "<ul>" +
                "<li>Đăng nhập vào tài khoản của bạn tại <a href='http://localhost:8080/workshophy/login' style='color: #4285F4; text-decoration: none;'>đây</a>.</li>" +
                "<li>Khám phá các workshop thú vị và đăng ký tham gia.</li>" +
                "<li>Kết nối với cộng đồng và theo dõi những người dùng khác.</li>" +
                "</ul>" +
                "<p>Nếu bạn có bất kỳ câu hỏi nào hoặc cần hỗ trợ, đừng ngần ngại liên hệ với chúng mình qua <a href='mailto:support@workshophy.com'>support@workshophy.com</a>.</p>" +
                "<p>Chúng mình rất mong được đồng hành cùng bạn trong hành trình này!</p>" +
                "<p>Thân ái,<br><strong>Đội ngũ Workshophy</strong></p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendWelcomeEmailForOrganizer(String toEmail, String fullName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Chào Mừng Nhà Cung Cấp Mới Đến Với Workshophy!");
        helper.setFrom("support@workshophy.com");

        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<h2>Chào Mừng Nhà Cung Cấp Mới!</h2>" +
                "<p>Xin chào " + (fullName != null && !fullName.isEmpty() ? fullName : "bạn") + ",</p>" +
                "<p>Chúng mình rất vui mừng chào đón bạn đến với cộng đồng Workshophy với vai trò là một nhà cung cấp! Tài khoản của bạn đã được xác thực thành công, và giờ đây bạn có thể bắt đầu hành trình chia sẻ kiến thức và kết nối với khách hàng trên nền tảng của chúng mình.</p>" +
                "<p><strong>Bắt đầu ngay:</strong></p>" +
                "<ul>" +
                "<li>Đăng nhập vào tài khoản của bạn tại <a href='http://localhost:8080/workshophy/login' style='color: #4285F4; text-decoration: none;'>đây</a>.</li>" +
                "<li>Tạo workshop đầu tiên của bạn và bắt đầu thu hút khách hàng.</li>" +
                "<li>Quản lý các sự kiện và tương tác với cộng đồng người dùng.</li>" +
                "</ul>" +
                "<p>Nếu bạn cần hỗ trợ trong việc thiết lập workshop hoặc có bất kỳ câu hỏi nào, đừng ngần ngại liên hệ với chúng mình qua <a href='mailto:support@workshophy.com'>support@workshophy.com</a>.</p>" +
                "<p>Chúng mình rất mong được đồng hành cùng bạn trong hành trình xây dựng những trải nghiệm tuyệt vời cho khách hàng!</p>" +
                "<p>Thân ái,<br><strong>Đội ngũ Workshophy</strong></p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
