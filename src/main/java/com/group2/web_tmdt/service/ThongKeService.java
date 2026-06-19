package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.DoanhThuDanhMucDTO;
import com.group2.web_tmdt.dto.DoanhThuNgayDTO;

import java.time.LocalDate;
import java.util.List;

public interface ThongKeService {
    List<DoanhThuNgayDTO> getDoanhThuTheoThang(int maSeller, int nam, int thang);

    List<DoanhThuDanhMucDTO> getDoanhThuTheoDanhMuc(int maSeller, int thang, int nam);
    List<DoanhThuNgayDTO> getDoanhThuTheoKhoangNgay(int maSeller, LocalDate tuNgay, LocalDate denNgay);
    List<DoanhThuDanhMucDTO> getDoanhThuTheoDanhMucKhoangNgay(int maSeller, LocalDate tuNgay, LocalDate denNgay);
}