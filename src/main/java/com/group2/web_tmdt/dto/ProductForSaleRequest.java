package com.group2.web_tmdt.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductForSaleRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private int soLuong;

    @Positive(message = "Giá bán phải lớn hơn 0")
    private double giaBan;

    @NotBlank(message = "Mô tả không được để trống")
    private String moTa;

    @NotBlank(message = "Màu sắc không được để trống")
    private String mauSac;

    @NotBlank(message = "Kích thước không được để trống")
    private String kichThuoc;

    @NotBlank(message = "Thương hiệu không được để trống")
    private String thuongHieu;

    @Min(value = 1, message = "Danh mục không hợp lệ")
    private int categoryId;

    @Min(value = 1, message = "Tình trạng không hợp lệ")
    private int tinhTrangId;

    @NotEmpty(message = "Phải có ít nhất 1 hình ảnh")
    @Size(max = 3, message = "Chỉ được tối đa 3 hình ảnh")
    @Valid
    private List<ProductImageRequest> images;
}
