package com.group2.web_tmdt.dto;

import lombok.Data;

@Data
public class TaoReviewRequest {
    private long maSanPham;
    private float diemXepHang; // 1-5
    private String nhanXet;
}
