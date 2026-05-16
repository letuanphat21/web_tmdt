package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.GioHangDTO;
import com.group2.web_tmdt.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;

    @GetMapping
    public ResponseEntity<ApiResponse<GioHangDTO>> getGioHang(
            @AuthenticationPrincipal UserDetails userDetails) {
        GioHangDTO cart = gioHangService.getGioHang(userDetails.getUsername());
        return ApiResponse.ok("Lấy giỏ hàng thành công!", cart);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<GioHangDTO>> themVaoGioHang(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Integer> body) {
        long maSanPham = body.get("maSanPham").longValue();
        int soLuong = body.getOrDefault("soLuong", 1);
        GioHangDTO cart = gioHangService.themVaoGioHang(userDetails.getUsername(), maSanPham, soLuong);
        return ApiResponse.ok("Thêm vào giỏ hàng thành công!", cart);
    }

    @DeleteMapping("/item/{maItem}")
    public ResponseEntity<ApiResponse<GioHangDTO>> xoaKhoiGioHang(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long maItem) {
        GioHangDTO cart = gioHangService.xoaKhoiGioHang(userDetails.getUsername(), maItem);
        return ApiResponse.ok("Xóa khỏi giỏ hàng thành công!", cart);
    }

    @PutMapping("/item/{maItem}")
    public ResponseEntity<ApiResponse<GioHangDTO>> capNhatSoLuong(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long maItem,
            @RequestBody Map<String, Integer> body) {
        int soLuong = body.get("soLuong");
        GioHangDTO cart = gioHangService.capNhatSoLuong(userDetails.getUsername(), maItem, soLuong);
        return ApiResponse.ok("Cập nhật giỏ hàng thành công!", cart);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> xoaGioHang(
            @AuthenticationPrincipal UserDetails userDetails) {
        gioHangService.xoaGioHang(userDetails.getUsername());
        return ApiResponse.ok("Xóa giỏ hàng thành công!");
    }
}
