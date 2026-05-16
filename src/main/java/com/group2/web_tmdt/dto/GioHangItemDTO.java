package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangItemDTO {

    private long maItem;

    private long maSanPham;

    private String tenSanPham;

    private double giaSanPham;

    private int soLuong;

    private String hinhAnh; // Hình ảnh đầu tiên của sản phẩm

    private String tenNguoiBan;

    private int soLuongTonKho;
}
