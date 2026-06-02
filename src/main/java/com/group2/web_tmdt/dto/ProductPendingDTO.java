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

    private String emailSeller;

    private String trangThai;

    private List<ProductImageResponse> images;

}
