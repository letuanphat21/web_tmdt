package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.RegisterRequest;
import com.group2.web_tmdt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * POST /api/auth/dang-ky
     *
     * Body (JSON):
     * {
     * "email": "user@example.com",
     * "matKhau": "123456",
     * "xacNhanMatKhau": "123456"
     * }
     */
    @PostMapping("/dang-ky")
    public ResponseEntity<ApiResponse<Void>> dangKy(@Validated @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.ok("Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản.");
    }

    /**
     * GET /api/auth/kich-hoat?ma={maKichHoat}
     */
    @GetMapping("/kich-hoat")
    public ResponseEntity<ApiResponse<Void>> kichHoat(@RequestParam("ma") String maKichHoat) {
        boolean thanhCong = userService.kichHoatTaiKhoan(maKichHoat);

        if (thanhCong) {
            return ApiResponse.ok("Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Mã kích hoạt không hợp lệ hoặc đã hết hạn.");
        }
    }
}
