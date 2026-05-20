package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageUpdateRequest {

    /** null hoặc 0 = thêm ảnh mới; có giá trị = cập nhật ảnh hiện có */
    private Integer maHinhAnh;

    private String tenAnh;

    private String duongDan;
}
