package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private long maSanPham;

    private String tenSanPham;

    private int soLuong;

    private double giaSanPham;

    private String tenTheLoai;

    private int maTheLoai;

    private String email; // Email của người bán

    private String tenNguoiBan; // Tên người bán

    private String hinhAnhDaiDien; // Hình ảnh đại diện (base64 hoặc URL)

    private double danhGia; // Trung bình đánh giá
}
