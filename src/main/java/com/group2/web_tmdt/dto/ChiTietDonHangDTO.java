package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangDTO {

    private long maChiTietDonHang;

    private long maSanPham;

    private String tenSanPham;

    private String hinhAnh;

    private int soLuong;

    private double giaBan;

    private double thanhTien;
}
