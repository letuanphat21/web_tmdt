package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.RegisterRequest;
import com.group2.web_tmdt.entity.User;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /**
     * Đăng ký tài khoản mới.
     * Trả về true nếu thành công, ném exception nếu email đã tồn tại.
     */
    void register(RegisterRequest request);

    /**
     * Kích hoạt tài khoản qua mã xác nhận gửi qua email.
     */
    boolean kichHoatTaiKhoan(String maKichHoat);

    Optional<User> findByEmail(String email);

    void quenMatKhau(String email);

    boolean xacNhanOtp(String email, String otp);

    void datLaiMatKhau(com.group2.web_tmdt.dto.ResetPasswordRequest request);
}
