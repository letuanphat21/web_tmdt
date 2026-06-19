package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.DoanhThuDanhMucDTO;
import com.group2.web_tmdt.dto.DoanhThuNgayDTO;
import com.group2.web_tmdt.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/thong-ke")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", maxAge = 3600)
@RestController
// ...
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("/seller/{maSeller}")
    public ResponseEntity<List<DoanhThuNgayDTO>> layDoanhThuCuaSeller(
            @PathVariable("maSeller") int maSeller,
            @RequestParam("nam") int nam,
            @RequestParam("thang") int thang) {

        List<DoanhThuNgayDTO> data = thongKeService.getDoanhThuTheoThang(maSeller, nam, thang);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/seller/{maSeller}/danh-muc")
    public ResponseEntity<List<DoanhThuDanhMucDTO>> getDoanhThuTheoDanhMuc(
            @PathVariable("maSeller") int maSeller, // Đổi thành int cho giống hàm ở trên
            @RequestParam("thang") int thang,
            @RequestParam("nam") int nam) {

        // Gọi qua Service thay vì Repository
        List<DoanhThuDanhMucDTO> result = thongKeService.getDoanhThuTheoDanhMuc(maSeller, thang, nam);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/seller/{maSeller}/khoang-ngay")
    public ResponseEntity<List<DoanhThuNgayDTO>> layDoanhThuTheoKhoangNgay(
            @PathVariable("maSeller") int maSeller,
            @RequestParam("tuNgay") String tuNgay,
            @RequestParam("denNgay") String denNgay) {
        LocalDate from = LocalDate.parse(tuNgay);
        LocalDate to = LocalDate.parse(denNgay);
        return ResponseEntity.ok(thongKeService.getDoanhThuTheoKhoangNgay(maSeller, from, to));
    }

    @GetMapping("/seller/{maSeller}/danh-muc/khoang-ngay")
    public ResponseEntity<List<DoanhThuDanhMucDTO>> layDoanhThuDanhMucTheoKhoangNgay(
            @PathVariable("maSeller") int maSeller,
            @RequestParam("tuNgay") String tuNgay,
            @RequestParam("denNgay") String denNgay) {
        LocalDate from = LocalDate.parse(tuNgay);
        LocalDate to = LocalDate.parse(denNgay);
        return ResponseEntity.ok(thongKeService.getDoanhThuTheoDanhMucKhoangNgay(maSeller, from, to));
    }
}