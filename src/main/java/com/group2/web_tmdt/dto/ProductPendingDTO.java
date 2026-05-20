package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPendingDTO {

    private Long maSanPham;

    private String tenSanPham;

    private int soLuong;

    private double giaSanPham;

    private String thuongHieu;

    private String moTa;

    private String mauSac;

    private String kichCo;

    private String emailSeller;

    private String tenTheLoai;

    private String tinhTrang;

    private String trangThai;

    private boolean active;

    private int soLuongDaBan;

    private List<ProductImageResponse> images;

}
