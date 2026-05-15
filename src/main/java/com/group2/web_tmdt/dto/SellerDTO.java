package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDTO {

    private long maNguoiDung;

    private String email;

    private String hoTen; // Tên đầy đủ (ghép hoDem + ten)

    private String soDienThoai;

    private String diaChi;

    private String avatar;

    private long soSanPham; // Số sản phẩm đang bán

    private double danhGiaXepHang; // Xếp hạng người bán
}
