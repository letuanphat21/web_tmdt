package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ThongKeRepository;
import com.group2.web_tmdt.dto.DoanhThuDanhMucDTO;
import com.group2.web_tmdt.dto.DoanhThuNgayDTO;
import com.group2.web_tmdt.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private ThongKeRepository thongKeRepository;

    @Override
    public List<DoanhThuNgayDTO> getDoanhThuTheoThang(int maSeller, int nam, int thang) {
        // Sử dụng đúng chữ "Hoàn thành" dựa theo UserBuyOrder.tsx của team
        return thongKeRepository.thongKeDoanhThuTheoThang(maSeller, nam, thang);
    }

    // ĐÃ THÊM HÀM NÀY CHO BẠN
    @Override
    public List<DoanhThuDanhMucDTO> getDoanhThuTheoDanhMuc(int maSeller, int thang, int nam) {
        return thongKeRepository.thongKeDoanhThuTheoDanhMuc(maSeller, thang, nam);
    }

    @Override
    public List<DoanhThuNgayDTO> getDoanhThuTheoKhoangNgay(int maSeller, LocalDate tuNgay, LocalDate denNgay) {
        return thongKeRepository.thongKeDoanhThuTheoKhoangNgay(maSeller, tuNgay, denNgay);
    }

    @Override
    public List<DoanhThuDanhMucDTO> getDoanhThuTheoDanhMucKhoangNgay(int maSeller, LocalDate tuNgay, LocalDate denNgay) {
        return thongKeRepository.thongKeDoanhThuTheoDanhMucKhoangNgay(maSeller, tuNgay, denNgay);
    }

    @Override
    public com.group2.web_tmdt.dto.AdminThongKeDTO getAdminThongKe(int nam) {
        double tongDoanhThu = thongKeRepository.countTongDoanhThu();
        long tongDonHang = thongKeRepository.countTongDonHang();
        long tongKhachHang = thongKeRepository.countTongKhachHang();
        long tongCuaHang = thongKeRepository.countTongCuaHang();
        List<com.group2.web_tmdt.dto.DoanhThuThangDTO> doanhThuTheoThang = thongKeRepository.thongKeDoanhThuNamTheoThang(nam);
        List<DoanhThuDanhMucDTO> doanhThuTheoDanhMuc = thongKeRepository.thongKeDoanhThuNamTheoDanhMuc(nam);

        return com.group2.web_tmdt.dto.AdminThongKeDTO.builder()
                .tongDoanhThu(tongDoanhThu)
                .tongDonHang(tongDonHang)
                .tongKhachHang(tongKhachHang)
                .tongCuaHang(tongCuaHang)
                .doanhThuTheoThang(doanhThuTheoThang)
                .doanhThuTheoDanhMuc(doanhThuTheoDanhMuc)
                .build();
    }
}