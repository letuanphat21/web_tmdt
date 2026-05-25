package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<DonHangDTO>>> taoDoHang(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        String diaChi = (String) body.get("diaChiNhanHang");
        double chiPhiGiaoHang = body.containsKey("chiPhiGiaoHang")
                ? ((Number) body.get("chiPhiGiaoHang")).doubleValue()
                : 30000;
        String phuongThucThanhToan = (String) body.getOrDefault("phuongThucThanhToan", "COD");

        if (diaChi == null || diaChi.isBlank()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập địa chỉ nhận hàng");
        }

        List<DonHangDTO> donHangs = donHangService.taoDoHang(
                userDetails.getUsername(), diaChi, chiPhiGiaoHang, phuongThucThanhToan);
        return ApiResponse.ok("Đặt hàng thành công!", donHangs);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DonHangDTO>>> getDonHang(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DonHangDTO> orders = donHangService.getDonHangCuaUser(userDetails.getUsername());
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công!", orders);
    }

    @PutMapping("/{maDonHang}/cancel")
    public ResponseEntity<ApiResponse<DonHangDTO>> huyDonHang(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int maDonHang,
            @RequestBody Map<String, Object> body) {
        String lyDoHuy = (String) body.getOrDefault("lyDoHuy", "Không có lý do");
        DonHangDTO donHang = donHangService.huyDonHang(
                userDetails.getUsername(), maDonHang, lyDoHuy);
        return ApiResponse.ok("Hủy đơn hàng thành công!", donHang);
    }

    @GetMapping("/sell-orders")
    public ResponseEntity<ApiResponse<List<DonHangDTO>>> getSellOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "status", defaultValue = "all") String trangThai) {
        List<DonHangDTO> orders = donHangService.getSellOrdersOfSeller(
                userDetails.getUsername(), trangThai);
        return ApiResponse.ok("Lấy danh sách đơn bán thành công!", orders);
    }

    @GetMapping("/{maDonHang}")
    public ResponseEntity<ApiResponse<DonHangDTO>> getDonHangById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int maDonHang) {
        DonHangDTO donHang = donHangService.getDonHangById(
                userDetails.getUsername(), maDonHang);
        return ApiResponse.ok("Lấy chi tiết đơn hàng thành công!", donHang);
    }

    @PutMapping("/{maDonHang}/seller-confirm")
    public ResponseEntity<ApiResponse<DonHangDTO>> xacNhanDonHang(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int maDonHang) {
        DonHangDTO donHang = donHangService.xacNhanDonHang(
                userDetails.getUsername(), maDonHang);
        return ApiResponse.ok("Xác nhận đơn hàng thành công!", donHang);
    }

    @PutMapping("/{maDonHang}/seller-cancel")
    public ResponseEntity<ApiResponse<DonHangDTO>> huyDonHangBySeller(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int maDonHang,
            @RequestBody Map<String, Object> body) {
        String lyDoHuy = (String) body.getOrDefault("lyDoHuy", "Người bán hủy đơn");
        DonHangDTO donHang = donHangService.huyDonHangBySeller(
                userDetails.getUsername(), maDonHang, lyDoHuy);
        return ApiResponse.ok("Hủy đơn hàng thành công!", donHang);
    }
}

