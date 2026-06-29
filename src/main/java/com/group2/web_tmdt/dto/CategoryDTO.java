package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Integer maTheLoai;

    private String tenTheLoai;

    private Long soSanPham; // Số sản phẩm trong danh mục

    private Boolean active;
}

