package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.JwtAuthResponse;
import com.group2.web_tmdt.dto.LoginRequest;
import com.group2.web_tmdt.dto.RegisterRequest;
import com.group2.web_tmdt.service.JWTService;
import com.group2.web_tmdt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

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
     * POST /api/auth/dang-nhap
     */
    @PostMapping("/dang-nhap")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> dangNhap(@Validated @RequestBody LoginRequest request,
            HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(request.getEmail());
            String refreshToken = jwtService.createRefreshToken(request.getEmail());

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
            // cookie.setSecure(true); // Bỏ comment nếu chạy HTTPS
            response.addCookie(cookie);

            return ApiResponse.ok("Đăng nhập thành công!", new JwtAuthResponse(token));
        } else {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng.");
        }
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
