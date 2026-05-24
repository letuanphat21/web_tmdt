package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHangDTO {

    private int maDonHang;

    private String ngayTao;

    private String diaChiNhanHang;

    private double chiPhiGiaoHang;

    private double tongTienSanPham;

    private double tongTien;

    private String trangThai; // "Chờ xác nhận" | "Đang giao" | "Hoàn thành" | "Đã hủy"

    private String lyDoHuy;

    private String phuongThucThanhToan; // "COD" | "VNPAY"

    private List<ChiTietDonHangDTO> chiTiet;

    private String tenKhachHang; // Tên khách hàng (người mua)
}
