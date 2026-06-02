package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ThongKeRepository;
import com.group2.web_tmdt.dto.DoanhThuDanhMucDTO;
import com.group2.web_tmdt.dto.DoanhThuNgayDTO;
import com.group2.web_tmdt.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}