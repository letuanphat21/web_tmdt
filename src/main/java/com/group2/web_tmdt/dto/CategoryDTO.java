package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private int maTheLoai;

    private String tenTheLoai;

    private long soSanPham; // Số sản phẩm trong danh mục
}
