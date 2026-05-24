package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dao.GiaoDichRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.entity.GiaoDich;
import com.group2.web_tmdt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vi")
@CrossOrigin(origins = "http://localhost:5173")
public class ViController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GiaoDichRepository giaoDichRepository;

    // 1. API Lấy số dư và lịch sử giao dịch
    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<?> layThongTinVi(@PathVariable Long maNguoiDung) {
        Optional<User> userOpt = userRepository.findById(maNguoiDung);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy user");

        User user = userOpt.get();
        List<GiaoDich> lichSu = giaoDichRepository.findByUser_MaNguoiDungOrderByNgayTaoDesc(maNguoiDung);

        Map<String, Object> response = new HashMap<>();
        response.put("soDu", user.getSoDu() != null ? user.getSoDu() : 0.0);
        response.put("lichSu", lichSu);

        return ResponseEntity.ok(response);
    }

    // 2. API Thực hiện rút tiền
    @PostMapping("/{maNguoiDung}/rut-tien")
    public ResponseEntity<?> rutTien(@PathVariable Long maNguoiDung, @RequestParam Double soTien) {
        Optional<User> userOpt = userRepository.findById(maNguoiDung);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy user");

        User user = userOpt.get();
        Double soDuHienTai = user.getSoDu() != null ? user.getSoDu() : 0.0;

        // Validate nghiệp vụ
        if (soTien < 50000) return ResponseEntity.badRequest().body("Số tiền rút tối thiểu là 50.000đ");
        if (soTien > soDuHienTai) return ResponseEntity.badRequest().body("Số dư không đủ");

        // 1. Trừ tiền thực tế trong DB
        user.setSoDu(soDuHienTai - soTien);
        userRepository.save(user);

        // 2. Lưu lịch sử giao dịch
        GiaoDich gd = new GiaoDich();
        gd.setUser(user);
        gd.setSoTien(soTien);
        gd.setLoaiGiaoDich("outflow");
        gd.setMoTa("Rút tiền về ngân hàng");
        gd.setTrangThai("Thành công"); // Giả định ngân hàng auto thành công
        giaoDichRepository.save(gd);

        return ResponseEntity.ok("Rút tiền thành công");
    }
}