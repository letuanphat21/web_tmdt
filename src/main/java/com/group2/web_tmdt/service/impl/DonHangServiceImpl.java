package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.*;
import com.group2.web_tmdt.dto.ChiTietDonHangDTO;
import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.entity.*;
import com.group2.web_tmdt.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonHangServiceImpl implements DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final GioHangRepository gioHangRepository;
    private final GioHangItemRepository gioHangItemRepository;
    private final UserRepository userRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final com.group2.web_tmdt.dao.ProductRepository productRepository;

    @Override
    @Transactional
    public DonHangDTO taoDoHang(String email, String diaChiNhanHang, double chiPhiGiaoHang) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        GioHang gioHang = gioHangRepository.findByUserMaNguoiDung(user.getMaNguoiDung())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống"));

        List<GioHangItem> items = gioHangItemRepository.findAllByGioHangId(gioHang.getMaGioHang());
        if (items.isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có sản phẩm");
        }

        // Tính tổng tiền sản phẩm
        double tongTienSanPham = items.stream()
                .mapToDouble(i -> i.getProduct().getGiaSanPham() * i.getSoLuong())
                .sum();

        // Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setUser(user);
        donHang.setNgayTao(Date.valueOf(LocalDate.now()));
        donHang.setDiaChiNhanHang(diaChiNhanHang);
        donHang.setChiPhiGiaoHang(chiPhiGiaoHang);
        donHang.setTongTienSanPham(tongTienSanPham);
        donHang.setTongTien(tongTienSanPham + chiPhiGiaoHang);
        donHang.setTrangThai("Chờ xác nhận");
        donHang = donHangRepository.save(donHang);

        // Tạo chi tiết đơn hàng
        List<ChiTietDonHang> chiTietList = new ArrayList<>();
        for (GioHangItem item : items) {
            ChiTietDonHang ct = new ChiTietDonHang();
            ct.setDonHang(donHang);
            ct.setProduct(item.getProduct());
            ct.setSoLuong(item.getSoLuong());
            ct.setGiaBan(item.getProduct().getGiaSanPham());
            // ma_sach là FK tới product, set bằng ma_san_pham của sản phẩm
            ct.setMaSach(item.getProduct().getMaSanPham());
            chiTietList.add(ct);
        }
        chiTietDonHangRepository.saveAll(chiTietList);

        // Xóa giỏ hàng sau khi đặt
        gioHangItemRepository.deleteAll(items);
        gioHangItemRepository.flush();

        return convertToDTO(donHang, chiTietList);
    }

    @Override
    public List<DonHangDTO> getDonHangCuaUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return donHangRepository
                .findByUserMaNguoiDungOrderByNgayTaoDesc(user.getMaNguoiDung())
                .stream()
                .map(dh -> convertToDTO(dh, dh.getChiTietDonHangs()))
                .collect(Collectors.toList());
    }

    @Override
    public DonHangDTO getDonHangById(String email, int maDonHang) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (donHang.getUser().getMaNguoiDung() != user.getMaNguoiDung()) {
            throw new RuntimeException("Không có quyền xem đơn hàng này");
        }

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    @Transactional
    public DonHangDTO huyDonHang(String email, int maDonHang, String lyDoHuy) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (donHang.getUser().getMaNguoiDung() != user.getMaNguoiDung()) {
            throw new RuntimeException("Không có quyền hủy đơn hàng này");
        }

        if (!"Chờ xác nhận".equals(donHang.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ xác nhận'");
        }

        donHang.setTrangThai("Đã hủy");
        donHang.setLyDoHuy(lyDoHuy != null ? lyDoHuy : "Không có lý do");
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    private DonHangDTO convertToDTO(DonHang dh, List<ChiTietDonHang> chiTietList) {
        DonHangDTO dto = new DonHangDTO();
        dto.setMaDonHang(dh.getMaDonHang());
        dto.setNgayTao(dh.getNgayTao() != null ? dh.getNgayTao().toString() : "");
        dto.setDiaChiNhanHang(dh.getDiaChiNhanHang());
        dto.setChiPhiGiaoHang(dh.getChiPhiGiaoHang());
        dto.setTongTienSanPham(dh.getTongTienSanPham());
        dto.setTongTien(dh.getTongTien());
        dto.setTrangThai(dh.getTrangThai());
        dto.setLyDoHuy(dh.getLyDoHuy());

        List<ChiTietDonHangDTO> ctDTOs = new ArrayList<>();
        if (chiTietList != null) {
            for (ChiTietDonHang ct : chiTietList) {
                ChiTietDonHangDTO ctDTO = new ChiTietDonHangDTO();
                ctDTO.setMaChiTietDonHang(ct.getMaChiTietDonHang());
                ctDTO.setMaSanPham(ct.getProduct().getMaSanPham());
                ctDTO.setTenSanPham(ct.getProduct().getTenSanPham());
                ctDTO.setSoLuong(ct.getSoLuong());
                ctDTO.setGiaBan(ct.getGiaBan());
                ctDTO.setThanhTien(ct.getGiaBan() * ct.getSoLuong());
                // Lấy hình ảnh đầu tiên
                if (ct.getProduct().getHinhAnhs() != null && !ct.getProduct().getHinhAnhs().isEmpty()) {
                    ctDTO.setHinhAnh(ct.getProduct().getHinhAnhs().get(0).getDuLieuAnh());
                }
                ctDTOs.add(ctDTO);
            }
        }
        dto.setChiTiet(ctDTOs);
        return dto;
    }

    @Override
    @Transactional
    public DonHangDTO hoanThanhDonHang(String email, int maDonHang) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Xác thực quyền (Chỉ người mua đơn hàng này mới được bấm Hoàn thành)
        if (donHang.getUser().getMaNguoiDung() != user.getMaNguoiDung()) {
            throw new RuntimeException("Không có quyền thao tác trên đơn hàng này");
        }

        // Chặn cộng tiền 2 lần
        if ("Thành công".equals(donHang.getTrangThai())) {
            throw new RuntimeException("Đơn hàng này đã được hoàn tất và cộng tiền trước đó!");
        }

        // 1. Chuyển trạng thái đơn hàng
        donHang.setTrangThai("Thành công");

        // 2. Duyệt qua từng sản phẩm trong đơn để cộng tiền cho đúng Seller
        for (ChiTietDonHang ct : donHang.getChiTietDonHangs()) {
            Product sanPham = ct.getProduct();
            User seller = sanPham.getUser(); // Truy xuất ra đúng người bán

            double soTienCong = ct.getGiaBan() * ct.getSoLuong();

            // A. Cộng tiền vào ví Seller
            double soDuHienTai = seller.getSoDu() != null ? seller.getSoDu() : 0.0;
            seller.setSoDu(soDuHienTai + soTienCong);
            userRepository.save(seller);

            // B. Ghi lịch sử giao dịch (Sao kê ví)
            GiaoDich giaoDich = new GiaoDich();
            giaoDich.setUser(seller);
            giaoDich.setSoTien(soTienCong);
            giaoDich.setLoaiGiaoDich("inflow"); // Mã đánh dấu tiền vào ví
            giaoDich.setTrangThai("Thành công");
            giaoDich.setMoTa("Tiền bán sản phẩm: " + sanPham.getTenSanPham() + " (Đơn #" + donHang.getMaDonHang() + ")");
            giaoDichRepository.save(giaoDich);

            // C. Tăng biến đếm số lượng đã bán của sản phẩm để làm thống kê
            sanPham.setSoLuongDaBan(sanPham.getSoLuongDaBan() + ct.getSoLuong());
            productRepository.save(sanPham);
        }

        donHangRepository.save(donHang);
        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }
}
