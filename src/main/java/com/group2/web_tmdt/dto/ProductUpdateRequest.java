package com.group2.web_tmdt.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private String tenSanPham;

    private Integer soLuong;

    private Double giaBan;

    private String moTa;

    private String mauSac;

    private String kichThuoc;

    private String thuongHieu;

    private Integer categoryId;

    private Integer tinhTrangId;

    /** Thêm mới (maHinhAnh null/0) hoặc cập nhật ảnh có sẵn */
    @Valid
    private List<ProductImageUpdateRequest> images;

    /** Danh sách mã ảnh cần xóa */
    private List<Integer> deleteImageIds;
}
