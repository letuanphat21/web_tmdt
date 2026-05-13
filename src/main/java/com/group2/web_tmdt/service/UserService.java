package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.RegisterRequest;

public interface UserService {

    /**
     * Đăng ký tài khoản mới.
     * Trả về true nếu thành công, ném exception nếu email đã tồn tại.
     */
    void register(RegisterRequest request);

    /**
     * Kích hoạt tài khoản qua mã xác nhận gửi qua email.
     */
    boolean kichHoatTaiKhoan(String maKichHoat);
}
