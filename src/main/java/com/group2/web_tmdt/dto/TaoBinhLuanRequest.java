package com.group2.web_tmdt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaoBinhLuanRequest {

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Long maSanPham;

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 1000, message = "Bình luận tối đa 1000 ký tự")
    private String noiDung;

    private Long maBinhLuanCha;
}
