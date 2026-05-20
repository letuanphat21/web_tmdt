package com.group2.web_tmdt.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSellerDTO {

    private Long maSanPham;

    private String tenSanPham;

    private int soLuong;

    private double giaSanPham;

    private String thuongHieu;

    private String moTa;

    private String mauSac;

    private String kichCo;

    private String trangThai;

    private String theLoai;

    private String tinhTrang;

    private boolean active;

    private int soLuongDaBan;

    private List<ProductImageSeller> images;
}
