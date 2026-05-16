package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private long maNguoiBan; // ID người bán

    private String tenNguoiBan; // Tên người bán

    private String hinhAnhDaiDien; // Avatar người bán (base64 hoặc URL)

    private double danhGia; // Trung bình đánh giá

    private int soLuongDanhGia; // Số lượng đánh giá

    // Danh sách hình ảnh sản phẩm (base64 hoặc URL)
    private List<String> hinhAnhs;

    // Danh sách đánh giá chi tiết
    private List<ReviewDTO> danhGias;
}
