package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.DonHangDTO;

import java.util.List;

public interface DonHangService {

    /** Tạo đơn hàng từ giỏ hàng hiện tại của user */
    DonHangDTO taoDoHang(String email, String diaChiNhanHang, double chiPhiGiaoHang);

    /** Lấy danh sách đơn hàng của user */
    List<DonHangDTO> getDonHangCuaUser(String email);

    /** Lấy chi tiết một đơn hàng */
    DonHangDTO getDonHangById(String email, int maDonHang);

    /** Hủy đơn hàng (chỉ được hủy khi đang "Chờ xác nhận") */
    DonHangDTO huyDonHang(String email, int maDonHang, String lyDoHuy);
}
