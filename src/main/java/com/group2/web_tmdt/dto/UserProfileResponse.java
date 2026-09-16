package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long maNguoiDung;
    private String email;
    private String hoDem;
    private String ten;
    private String soDienThoai;
    private String diaChi;
    private Character gioiTinh;
    private String avatar;
    private String hobby;
    private String googleId;
    private LocalDate birthDay;
    private LocalDateTime ngayDangKy;
    private LocalDateTime thoiGianChinhSua;
}
