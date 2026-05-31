package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ChiTietDonHangRepository;
import com.group2.web_tmdt.dao.DonHangRepository;
import com.group2.web_tmdt.dao.GioHangItemRepository;
import com.group2.web_tmdt.dao.GioHangRepository;
import com.group2.web_tmdt.dao.TrangThaiDonHangRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.ChiTietDonHangDTO;
import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.dto.PageResponse;
import com.group2.web_tmdt.entity.ChiTietDonHang;
import com.group2.web_tmdt.entity.DonHang;
import com.group2.web_tmdt.entity.GioHang;
import com.group2.web_tmdt.entity.GioHangItem;
import com.group2.web_tmdt.entity.TrangThaiDonHang;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.service.DonHangService;
import com.group2.web_tmdt.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonHangServiceImpl implements DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final GioHangRepository gioHangRepository;
    private final GioHangItemRepository gioHangItemRepository;
    private final UserRepository userRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final EmailService emailService;

    /**
     * Tạo đơn hàng — chia theo từng seller.
     *
     * Luồng:
     * 1. Lấy giỏ hàng của user
     * 2. Group items theo seller (product.user)
     * 3. Mỗi seller → tạo 1 DonHang riêng
     * 4. Tất cả đơn con dùng chung maDonHangCha (= maDonHang của đơn đầu tiên)
     * 5. Gửi email thông báo cho từng seller (async)
     * 6. Xóa giỏ hàng
     * 7. Trả về list tất cả đơn con
     */
    @Override
    @Transactional
    public List<DonHangDTO> taoDoHang(String email, String diaChiNhanHang,
                                       double chiPhiGiaoHang, String phuongThucThanhToan) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        GioHang gioHang = gioHangRepository.findByUserMaNguoiDung(user.getMaNguoiDung())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống"));

        List<GioHangItem> items = gioHangItemRepository.findAllByGioHangId(gioHang.getMaGioHang());
        if (items.isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có sản phẩm");
        }

        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByTenTrangThai("Chờ duyệt")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Chờ duyệt'"));

        // ── Group items theo seller ──────────────────────────────────────────
        Map<Long, List<GioHangItem>> itemsBySeller = items.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getProduct().getUser().getMaNguoiDung(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Chia phí giao hàng đều cho từng đơn con
        double chiPhiMoiDon = itemsBySeller.size() > 0
                ? chiPhiGiaoHang / itemsBySeller.size()
                : chiPhiGiaoHang;

        String phuongThuc = phuongThucThanhToan != null ? phuongThucThanhToan : "COD";

        // Tên người mua để gửi email
        String tenNguoiMua = buildTenNguoiDung(user);

        List<DonHang> savedDonHangs = new ArrayList<>();
        List<List<ChiTietDonHang>> savedChiTiets = new ArrayList<>();
        Integer maDonHangCha = null;

        for (Map.Entry<Long, List<GioHangItem>> entry : itemsBySeller.entrySet()) {
            List<GioHangItem> sellerItems = entry.getValue();
            User seller = sellerItems.get(0).getProduct().getUser();

            double tongTienSanPham = sellerItems.stream()
                    .mapToDouble(i -> i.getProduct().getGiaSanPham() * i.getSoLuong())
                    .sum();

            // Tạo đơn hàng cho seller này
            DonHang donHang = new DonHang();
            donHang.setUser(user);
            donHang.setNgayTao(Date.valueOf(LocalDate.now()));
            donHang.setDiaChiNhanHang(diaChiNhanHang);
            donHang.setChiPhiGiaoHang(chiPhiMoiDon);
            donHang.setTongTienSanPham(tongTienSanPham);
            donHang.setTongTien(tongTienSanPham + chiPhiMoiDon);
            donHang.setTrangThaiDonHang(trangThai);
            donHang.setPhuongThucThanhToan(phuongThuc);
            donHang.setMaDonHangCha(maDonHangCha); // null cho đơn đầu tiên

            donHang = donHangRepository.save(donHang);

            // Đơn đầu tiên → dùng id của nó làm maDonHangCha cho tất cả
            if (maDonHangCha == null) {
                maDonHangCha = donHang.getMaDonHang();
                donHang.setMaDonHangCha(maDonHangCha);
                donHang = donHangRepository.save(donHang);
            }

            // Tạo chi tiết đơn hàng
            List<ChiTietDonHang> chiTietList = new ArrayList<>();
            for (GioHangItem item : sellerItems) {
                ChiTietDonHang ct = new ChiTietDonHang();
                ct.setDonHang(donHang);
                ct.setProduct(item.getProduct());
                ct.setSoLuong(item.getSoLuong());
                ct.setGiaBan(item.getProduct().getGiaSanPham());
                ct.setMaSach(item.getProduct().getMaSanPham());
                chiTietList.add(ct);
            }
            chiTietDonHangRepository.saveAll(chiTietList);

            savedDonHangs.add(donHang);
            savedChiTiets.add(chiTietList);

            // ── Gửi email cho seller (async — không block transaction) ────────
            try {
                StringBuilder chiTietText = new StringBuilder();
                for (GioHangItem item : sellerItems) {
                    chiTietText.append("  - ")
                            .append(item.getProduct().getTenSanPham())
                            .append(" x").append(item.getSoLuong())
                            .append(" (").append(String.format("%,.0f", item.getProduct().getGiaSanPham()))
                            .append(" VND)\n");
                }
                emailService.guiEmailDonHangMoiChoSeller(
                        seller.getEmail(),
                        tenNguoiMua,
                        donHang.getMaDonHang(),
                        diaChiNhanHang,
                        donHang.getTongTien(),
                        chiTietText.toString()
                );
            } catch (Exception e) {
                // Lỗi email không được làm rollback transaction
                System.err.println("[EMAIL ERROR] Seller " + seller.getEmail() + ": " + e.getMessage());
            }
        }

        // Xóa giỏ hàng sau khi tạo đơn thành công
        gioHangItemRepository.deleteAll(items);
        gioHangItemRepository.flush();

        // Build kết quả trả về
        List<DonHangDTO> result = new ArrayList<>();
        for (int i = 0; i < savedDonHangs.size(); i++) {
            result.add(convertToDTO(savedDonHangs.get(i), savedChiTiets.get(i)));
        }
        return result;
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

        if (donHang.getTrangThaiDonHang() == null
                || !"Chờ duyệt".equals(donHang.getTrangThaiDonHang().getTenTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiHuy = trangThaiDonHangRepository.findByTenTrangThai("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã hủy'"));
        donHang.setTrangThaiDonHang(trangThaiHuy);
        donHang.setLyDoHuy(lyDoHuy != null ? lyDoHuy : "Không có lý do");
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    public List<DonHangDTO> getSellOrdersOfSeller(String email, String trangThai) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<DonHang> allOrders = donHangRepository.findAllWithDetails();

        return allOrders.stream()
                .filter(dh -> {
                    boolean hasSellersProduct = dh.getChiTietDonHangs().stream()
                            .anyMatch(ct -> ct.getProduct() != null
                                    && ct.getProduct().getUser() != null
                                    && ct.getProduct().getUser().getMaNguoiDung() == seller.getMaNguoiDung());
                    if (!hasSellersProduct) return false;
                    if ("all".equalsIgnoreCase(trangThai)) return true;
                    String currentStatus = dh.getTrangThaiDonHang() != null
                            ? dh.getTrangThaiDonHang().getTenTrangThai() : "";
                    return currentStatus.equalsIgnoreCase(trangThai);
                })
                .sorted((o1, o2) -> {
                    if (o1.getNgayTao() == null || o2.getNgayTao() == null) return 0;
                    return o2.getNgayTao().compareTo(o1.getNgayTao());
                })
                .map(dh -> convertToDTO(dh, dh.getChiTietDonHangs()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonHangDTO xacNhanDonHang(String sellerEmail, int maDonHang) {
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String currentStatus = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể xác nhận đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiDaDuyet = trangThaiDonHangRepository.findByTenTrangThai("Đã duyệt")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã duyệt'"));
        donHang.setTrangThaiDonHang(trangThaiDaDuyet);
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    @Transactional
    public DonHangDTO huyDonHangBySeller(String sellerEmail, int maDonHang, String lyDoHuy) {
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String currentStatus = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiHuy = trangThaiDonHangRepository.findByTenTrangThai("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã hủy'"));
        donHang.setTrangThaiDonHang(trangThaiHuy);
        donHang.setLyDoHuy(lyDoHuy != null ? lyDoHuy : "Người bán hủy đơn");
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DonHangDTO> getAllOrdersForAdmin(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<DonHang> orderPage = "all".equalsIgnoreCase(status)
                ? donHangRepository.findAll(pageable)
                : donHangRepository.findByTrangThaiDonHangTenTrangThaiIgnoreCase(status, pageable);

        List<DonHangDTO> content = orderPage.getContent().stream()
                .map(dh -> convertToDTO(dh, dh.getChiTietDonHangs()))
                .collect(Collectors.toList());

        return PageResponse.<DonHangDTO>builder()
                .content(content)
                .currentPage(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public DonHangDTO adminConfirmOrder(int maDonHang) {
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String currentStatus = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể xác nhận đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiDaDuyet = trangThaiDonHangRepository.findByTenTrangThai("Đã duyệt")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã duyệt'"));
        donHang.setTrangThaiDonHang(trangThaiDaDuyet);
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    @Transactional
    public DonHangDTO adminCancelOrder(int maDonHang, String lyDoHuy) {
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String currentStatus = donHang.getTrangThaiDonHang() != null
                ? donHang.getTrangThaiDonHang().getTenTrangThai() : "";
        if (!"Chờ duyệt".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiHuy = trangThaiDonHangRepository.findByTenTrangThai("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã hủy'"));
        donHang.setTrangThaiDonHang(trangThaiHuy);
        donHang.setLyDoHuy(lyDoHuy != null ? lyDoHuy : "Admin hủy đơn");
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    @Override
    @Transactional
    public DonHangDTO updateOrderStatus(int maDonHang, String trangThaiMoi) {
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByTenTrangThai(trangThaiMoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + trangThaiMoi));
        
        donHang.setTrangThaiDonHang(trangThai);
        donHangRepository.save(donHang);

        return convertToDTO(donHang, donHang.getChiTietDonHangs());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private DonHangDTO convertToDTO(DonHang dh, List<ChiTietDonHang> chiTietList) {
        DonHangDTO dto = new DonHangDTO();
        dto.setMaDonHang(dh.getMaDonHang());
        dto.setNgayTao(dh.getNgayTao() != null ? dh.getNgayTao().toString() : "");
        dto.setDiaChiNhanHang(dh.getDiaChiNhanHang());
        dto.setChiPhiGiaoHang(dh.getChiPhiGiaoHang());
        dto.setTongTienSanPham(dh.getTongTienSanPham());
        dto.setTongTien(dh.getTongTien());
        dto.setTrangThai(dh.getTrangThaiDonHang() != null
                ? dh.getTrangThaiDonHang().getTenTrangThai() : "");
        dto.setLyDoHuy(dh.getLyDoHuy());
        dto.setPhuongThucThanhToan(dh.getPhuongThucThanhToan());
        dto.setMaDonHangCha(dh.getMaDonHangCha());

        // Tên người mua
        if (dh.getUser() != null) {
            dto.setTenKhachHang(buildTenNguoiDung(dh.getUser()));
        }

        // Tên người bán — lấy từ sản phẩm đầu tiên trong đơn
        if (chiTietList != null && !chiTietList.isEmpty()) {
            var firstProduct = chiTietList.get(0).getProduct();
            if (firstProduct != null && firstProduct.getUser() != null) {
                User seller = firstProduct.getUser();
                dto.setTenNguoiBan(buildTenNguoiDung(seller));
                dto.setEmailNguoiBan(seller.getEmail());
            }
        }

        // Chi tiết sản phẩm
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
                // Ảnh: ưu tiên duongDan (Supabase URL), fallback duLieuAnh (base64)
                if (ct.getProduct().getHinhAnhs() != null && !ct.getProduct().getHinhAnhs().isEmpty()) {
                    var hinhAnh = ct.getProduct().getHinhAnhs().get(0);
                    String imgUrl = hinhAnh.getDuongDan() != null && !hinhAnh.getDuongDan().isBlank()
                            ? hinhAnh.getDuongDan()
                            : hinhAnh.getDuLieuAnh();
                    ctDTO.setHinhAnh(imgUrl);
                }
                ctDTOs.add(ctDTO);
            }
        }
        dto.setChiTiet(ctDTOs);
        return dto;
    }

    private String buildTenNguoiDung(User user) {
        String ten = ((user.getHoDem() != null ? user.getHoDem() : "")
                + " " + (user.getTen() != null ? user.getTen() : "")).trim();
        return ten.isBlank() ? user.getEmail() : ten;
    }
}
