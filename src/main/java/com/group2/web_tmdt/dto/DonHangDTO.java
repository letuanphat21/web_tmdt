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
    private String tenNguoiBan;           // tên seller của đơn này
    private String emailNguoiBan;         // email seller
    private List<ChiTietDonHangDTO> chiTiet;
    private String tenKhachHang; // Tên khách hàng (người mua)

    private String sdtKhachHang; // Số điện thoại khách hàng

    private String emailKhachHang; // Email khách hàng (người mua)

    private String tenShop; // Tên shop bán hàng

    private String sdtShop; // Số điện thoại shop bán hàng

}
