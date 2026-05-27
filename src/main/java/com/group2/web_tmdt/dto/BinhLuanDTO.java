package com.group2.web_tmdt.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BinhLuanDTO {

    private long maBinhLuan;
    private String noiDung;
    private LocalDateTime thoiGianTao;

    // Thông tin người bình luận
    private String emailNguoiDung;
    private String tenNguoiDung;
    private String avatarNguoiDung;

    // null nếu là bình luận gốc
    private Long maBinhLuanCha;

    // Danh sách trả lời (chỉ load cho bình luận gốc)
    private List<BinhLuanDTO> traLoi;
}
