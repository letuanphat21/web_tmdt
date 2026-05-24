package com.group2.web_tmdt.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface VNPayService {

    /**
     * Tạo URL thanh toán VNPAY cho một đơn hàng.
     * @param maDonHang  mã đơn hàng
     * @param tongTien   tổng tiền (VNĐ)
     * @param ipAddress  IP của client
     * @return URL redirect sang VNPAY
     */
    String taoUrlThanhToan(int maDonHang, long tongTien, String ipAddress);

    /**
     * Xác thực chữ ký (secure hash) từ VNPAY trả về.
     * @param params tất cả query params từ VNPAY
     * @return true nếu chữ ký hợp lệ
     */
    boolean xacThucChuKy(Map<String, String> params);

    /**
     * Lấy IP thực của client từ request (hỗ trợ proxy/nginx).
     */
    String layIpAddress(HttpServletRequest request);
}
