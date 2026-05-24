package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dao.DonHangRepository;
import com.group2.web_tmdt.dao.TrangThaiDonHangRepository;
import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.entity.DonHang;
import com.group2.web_tmdt.entity.TrangThaiDonHang;
import com.group2.web_tmdt.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;
    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;

    /**
     * POST /api/payment/vnpay/create-payment
     * Tạo URL thanh toán VNPAY cho đơn hàng đã tạo.
     * Body: { "maDonHang": 1 }
     */
    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<Map<String, String>>> taoThanhToan(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request) {

        Integer maDonHang = body.get("maDonHang");
        if (maDonHang == null) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Thiếu mã đơn hàng");
        }

        DonHang donHang = donHangRepository.findById(maDonHang).orElse(null);
        if (donHang == null) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng");
        }

        // Kiểm tra đơn hàng thuộc về user đang đăng nhập
        if (!donHang.getUser().getEmail().equals(userDetails.getUsername())) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "Không có quyền thanh toán đơn hàng này");
        }

        // Chỉ cho phép thanh toán đơn ở trạng thái "Chờ duyệt"
        String trangThai = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(trangThai)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Đơn hàng không ở trạng thái có thể thanh toán");
        }

        long tongTien = (long) donHang.getTongTien();
        String ipAddress = vnPayService.layIpAddress(request);
        String paymentUrl = vnPayService.taoUrlThanhToan(maDonHang, tongTien, ipAddress);

        Map<String, String> result = new HashMap<>();
        result.put("paymentUrl", paymentUrl);
        return ApiResponse.ok("Tạo URL thanh toán thành công!", result);
    }

    /**
     * GET /api/payment/vnpay/ipn
     * IPN — VNPAY gọi server-to-server để thông báo kết quả thanh toán.
     * Không cần JWT (public endpoint).
     */
    @GetMapping("/ipn")
    @Transactional
    public ResponseEntity<Map<String, String>> ipnHandler(
            @RequestParam Map<String, String> params) {

        Map<String, String> response = new HashMap<>();

        // 1. Xác thực chữ ký
        if (!vnPayService.xacThucChuKy(params)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            return ResponseEntity.ok(response);
        }

        // 2. Lấy mã đơn hàng từ vnp_TxnRef (format: maDonHang_timestamp)
        String vnpTxnRef = params.get("vnp_TxnRef");
        if (vnpTxnRef == null) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return ResponseEntity.ok(response);
        }

        int maDonHang;
        try {
            maDonHang = Integer.parseInt(vnpTxnRef.split("_")[0]);
        } catch (NumberFormatException e) {
            response.put("RspCode", "01");
            response.put("Message", "Invalid TxnRef");
            return ResponseEntity.ok(response);
        }

        DonHang donHang = donHangRepository.findById(maDonHang).orElse(null);
        if (donHang == null) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return ResponseEntity.ok(response);
        }

        // 3. Kiểm tra số tiền khớp
        long vnpAmount = Long.parseLong(params.getOrDefault("vnp_Amount", "0")) / 100;
        long tongTienDonHang = (long) donHang.getTongTien();
        if (vnpAmount != tongTienDonHang) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid Amount");
            return ResponseEntity.ok(response);
        }

        // 4. Kiểm tra đơn hàng đã xử lý chưa (tránh xử lý 2 lần)
        String trangThaiHienTai = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(trangThaiHienTai)) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return ResponseEntity.ok(response);
        }

        // 5. Cập nhật trạng thái theo kết quả
        String vnpResponseCode = params.getOrDefault("vnp_ResponseCode", "");
        if ("00".equals(vnpResponseCode)) {
            // Thanh toán thành công → "Đã thanh toán" (fallback "Đã duyệt")
            TrangThaiDonHang trangThaiMoi = trangThaiDonHangRepository
                    .findByTenTrangThai("Đã thanh toán")
                    .orElseGet(() -> trangThaiDonHangRepository
                            .findByTenTrangThai("Đã duyệt")
                            .orElse(null));
            if (trangThaiMoi != null) {
                donHang.setTrangThaiDonHang(trangThaiMoi);
                donHang.setPhuongThucThanhToan("VNPAY");
                donHangRepository.save(donHang);
            }
        } else {
            // Thanh toán thất bại → "Đã hủy"
            TrangThaiDonHang trangThaiHuy = trangThaiDonHangRepository
                    .findByTenTrangThai("Đã hủy").orElse(null);
            if (trangThaiHuy != null) {
                donHang.setTrangThaiDonHang(trangThaiHuy);
                donHang.setLyDoHuy("Thanh toán VNPAY thất bại (mã: " + vnpResponseCode + ")");
                donHangRepository.save(donHang);
            }
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return ResponseEntity.ok(response);
    }
}
