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
    private String trangThai;
    private String lyDoHuy;
    private String phuongThucThanhToan;   // "COD" | "VNPAY"
    private Integer maDonHangCha;         // group các đơn con cùng 1 lần checkout
    private String tenKhachHang;          // tên người mua
    private String tenNguoiBan;           // tên seller của đơn này
    private List<ChiTietDonHangDTO> chiTiet;
}
