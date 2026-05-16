package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private long maDanhGia;

    private float diemXepHang;

    private String nhanXet;

    private String emailNguoiDung;

    private String tenNguoiDung;

    private String avatarNguoiDung;
}
