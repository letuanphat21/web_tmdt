package com.group2.web_tmdt.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateProfileRequest {

    private String avatar;

    private String hoDem;

    private String ten;

    private LocalDate birthDay;

    /** 'M' = Nam, 'F' = Nữ, 'O' = Khác */
    private Character gioiTinh;

    private String diaChi;

    private String soDienThoai;

    private String hobby;
}
