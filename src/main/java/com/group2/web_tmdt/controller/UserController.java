package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.UpdateProfileRequest;
import com.group2.web_tmdt.dto.UserProfileResponse;
import com.group2.web_tmdt.service.UserService;
import com.group2.web_tmdt.websocket.dto.ConversationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/user/profile
     * Lấy thông tin hồ sơ cá nhân của người dùng đang đăng nhập.
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserProfileResponse profile = userService.getProfile(userDetails.getUsername());
        return ApiResponse.ok("Lấy thông tin hồ sơ thành công!", profile);
    }

    /**
     * PUT /api/user/profile
     * Cập nhật thông tin hồ sơ cá nhân.
     * Header: Authorization: Bearer <token>
     *
     * Body (JSON) — tất cả field đều optional, chỉ gửi field muốn cập nhật:
     * {
     * "avatar": "https://...",
     * "hoDem": "Nguyễn Văn",
     * "ten": "An",
     * "birthDay": "1999-05-16T00:00:00",
     * "gioiTinh": "M",
     * "diaChi": "Hà Nội",
     * "soDienThoai": "0901234567",
     * "hobby": "Đọc sách, du lịch"
     * }
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {

        UserProfileResponse updated = userService.updateProfile(userDetails.getUsername(), request);
        return ApiResponse.ok("Cập nhật hồ sơ thành công!", updated);
    }


}
