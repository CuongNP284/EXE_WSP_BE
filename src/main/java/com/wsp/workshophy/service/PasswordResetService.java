package com.wsp.workshophy.service;

import com.wsp.workshophy.dto.request.ResetPasswordRequest;
import com.wsp.workshophy.dto.request.VerifyOtpRequest;
import com.wsp.workshophy.entity.Otp;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.repository.OtpRepository;
import com.wsp.workshophy.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PasswordResetService {
    UserRepository userRepository;
    OtpRepository otpRepository;
    JavaMailSender mailSender;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${spring.mail.username}")
    String senderEmail;

    @NonFinal
    @Value("${otp.expiration}")
    long otpExpiration;

    // Bước 1: Yêu cầu reset mật khẩu và gửi OTP qua email
    public void requestPasswordReset(ResetPasswordRequest request) {
        // Kiểm tra user tồn tại
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo mã OTP
        String otpCode = generateOtpCode();

        // Lưu OTP vào cơ sở dữ liệu
        Otp otp = Otp.builder()
                .otpCode(otpCode)
                .email(user.getEmail())
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpiration))
                .used(false)
                .build();
        otpRepository.save(otp);

        // Gửi email chứa OTP
        try {
            sendOtpEmail(user.getEmail(), user.getFirstName(), otpCode, otpExpiration);
        } catch (MailException e) {
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // Bước 2: Xác nhận OTP và đặt lại mật khẩu
    public void resetPassword(VerifyOtpRequest request) {
        // Tìm OTP hợp lệ
        Otp otp = otpRepository.findByEmailAndOtpCodeAndUsedFalseAndExpiresAtAfter(
                        request.getEmail(), request.getOtpCode(), LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        // Tìm user
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Đánh dấu OTP đã sử dụng
        otp.setUsed(true);
        otpRepository.save(otp);

        // Đặt lại mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Tạo mã OTP ngẫu nhiên (6 chữ số)
    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Tạo số ngẫu nhiên 6 chữ số
        return String.valueOf(otp);
    }

    // Gửi email chứa OTP
    public void sendOtpEmail(String toEmail, String firstName, String otpCode, long otpExpiration) throws MessagingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thiết lập thông tin email
        helper.setTo(toEmail);
        helper.setSubject("Workshophy - Password Reset OTP");
        helper.setFrom("support@workshophy.com");

        // Cá nhân hóa thông điệp với HTML
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<h2>Yêu Cầu Đặt Lại Mật Khẩu</h2>" +
                "<p>Xin chào " + (firstName != null && !firstName.isEmpty() ? firstName : "bạn") + ",</p>" +
                "<p>Chúng mình vừa nhận được yêu cầu đặt lại mật khẩu cho tài khoản Workshophy của bạn. Để tiếp tục, bạn hãy sử dụng Mã Xác Nhận Một Lần (OTP) sau đây nhé:</p>" +
                "<h3 style='color: #4285F4;'>Mã OTP của bạn: <strong>" + otpCode + "</strong></h3>" +
                "<p>Mã OTP này chỉ có hiệu lực trong <strong>" + (otpExpiration / 60) + " phút</strong> thôi. Bạn hãy nhập mã này vào biểu mẫu đặt lại mật khẩu để tiếp tục nha.</p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, bạn có thể bỏ qua email này hoặc liên hệ ngay với đội ngũ hỗ trợ của chúng mình qua <a href='mailto:support@workshophy.com'>support@workshophy.com</a> để bảo vệ tài khoản của bạn.</p>" +
                "<p>Để đảm bảo an toàn, bạn đừng chia sẻ mã OTP này với bất kỳ ai nhé. Nếu bạn cần hỗ trợ thêm, cứ liên hệ với chúng mình bất cứ lúc nào!</p>" +
                "<p>Cảm ơn bạn đã chọn Workshophy!</p>" +
                "<p>Thân ái,<br><strong>Đội ngũ Workshophy</strong></p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true); // true: gửi dưới dạng HTML

        // Gửi email
        mailSender.send(message);
    }
}
