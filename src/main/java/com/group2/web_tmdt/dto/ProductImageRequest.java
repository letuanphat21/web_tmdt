package com.group2.web_tmdt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {

    @NotBlank(message = "Tên ảnh không được để trống")
    private String tenAnh;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    private String duongDan;
}
