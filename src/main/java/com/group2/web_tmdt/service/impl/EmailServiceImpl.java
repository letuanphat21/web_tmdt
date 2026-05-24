package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void guiEmailKichHoat(String toEmail, String maKichHoat) {
        String linkKichHoat = "http://localhost:5173/kich-hoat/" + maKichHoat;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kích hoạt tài khoản của bạn");
        message.setText(
                "Chào bạn,\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản.\n" +
                "Vui lòng nhấn vào đường dẫn bên dưới để kích hoạt tài khoản:\n\n" +
                linkKichHoat + "\n\n" +
                "Đường dẫn có hiệu lực trong 24 giờ.\n\n" +
                "Trân trọng,\nWeb TMĐT Team"
        );
        mailSender.send(message);
    }

    @Async
    @Override
    public void guiEmailQuenMatKhau(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText(
                "Chào bạn,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.\n" +
                "Mã xác nhận (OTP) của bạn là: " + otp + "\n\n" +
                "Mã này có hiệu lực trong 24 giờ.\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\nWeb TMĐT Team"
        );
        mailSender.send(message);
    }

    @Override
    public void guiEmailTuChoi(String toEmail, String lyDo) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Thông báo từ chối sản phẩm");

        message.setText(
                "Xin chào,\n\n" +
                        "Sản phẩm của bạn đã bị từ chối bởi hệ thống kiểm duyệt.\n\n" +
                        "Lý do từ chối: " + lyDo + "\n\n" +
                        "Vui lòng kiểm tra và cập nhật lại thông tin sản phẩm trước khi gửi duyệt lại.\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ quản trị."
        );

        mailSender.send(message);
    }
}
