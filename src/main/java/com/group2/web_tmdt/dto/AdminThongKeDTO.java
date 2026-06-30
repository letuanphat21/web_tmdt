package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminThongKeDTO {
    private double tongDoanhThu;
    private long tongDonHang;
    private long tongKhachHang;
    private long tongCuaHang;
    private List<DoanhThuThangDTO> doanhThuTheoThang;
    private List<DoanhThuDanhMucDTO> doanhThuTheoDanhMuc;
}
