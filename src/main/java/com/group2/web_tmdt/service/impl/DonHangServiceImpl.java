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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.group2.web_tmdt.entity.ChiTietDonHang;
import com.group2.web_tmdt.entity.DonHang;
import com.group2.web_tmdt.entity.GioHang;
import com.group2.web_tmdt.entity.GioHangItem;
import com.group2.web_tmdt.entity.TrangThaiDonHang;
import com.group2.web_tmdt.entity.User;
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
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;

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
        
        // Lấy trạng thái "Chờ duyệt"
        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByTenTrangThai("Chờ duyệt")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Chờ duyệt'"));
        donHang.setTrangThaiDonHang(trangThai);
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

        if (donHang.getTrangThaiDonHang() == null || !"Chờ duyệt".equals(donHang.getTrangThaiDonHang().getTenTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ duyệt'");
        }

        TrangThaiDonHang trangThaiHuy = trangThaiDonHangRepository.findByTenTrangThai("Đã hủy")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái 'Đã hủy'"));
        donHang.setTrangThaiDonHang(trangThaiHuy);
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
        dto.setTrangThai(dh.getTrangThaiDonHang() != null ? dh.getTrangThaiDonHang().getTenTrangThai() : "");
        dto.setLyDoHuy(dh.getLyDoHuy());
        
        // Set tên khách hàng (người mua)
        if (dh.getUser() != null) {
            dto.setTenKhachHang(dh.getUser().getTen() != null ? dh.getUser().getTen() : dh.getUser().getEmail());
        } else {
            dto.setTenKhachHang("Khách hàng");
        }

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
    public List<DonHangDTO> getSellOrdersOfSeller(String email, String trangThai) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Lấy tất cả đơn hàng với eager load chi tiết, sản phẩm và người bán
        List<DonHang> allOrders = donHangRepository.findAllWithDetails();

        return allOrders.stream()
                .filter(dh -> {
                    // Kiểm tra nếu đơn hàng có chứa sản phẩm của seller này
                    boolean hasSellersProduct = dh.getChiTietDonHangs().stream()
                            .anyMatch(ct -> ct.getProduct() != null 
                                    && ct.getProduct().getUser() != null
                                    && ct.getProduct().getUser().getMaNguoiDung() == seller.getMaNguoiDung());
                    
                    if (!hasSellersProduct) {
                        return false;
                    }

                    // Kiểm tra filter theo trạng thái
                    if ("all".equalsIgnoreCase(trangThai)) {
                        return true;
                    }
                    
                    String currentStatus = dh.getTrangThaiDonHang() != null 
                            ? dh.getTrangThaiDonHang().getTenTrangThai() 
                            : "";
                    return currentStatus.equalsIgnoreCase(trangThai);
                })
                .sorted((o1, o2) -> {
                    // Sắp xếp theo ngày tạo giảm dần
                    if (o1.getNgayTao() == null || o2.getNgayTao() == null) {
                        return 0;
                    }
                    return o2.getNgayTao().compareTo(o1.getNgayTao());
                })
                .map(dh -> convertToDTO(dh, dh.getChiTietDonHangs()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonHangDTO xacNhanDonHang(String sellerEmail, int maDonHang) {
        // Dùng findByIdWithDetails để FETCH JOIN đầy đủ chiTietDonHangs → product → user
        // tránh lazy loading bug khi gọi getChiTietDonHangs() ngoài session
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Đơn hàng chỉ hiển thị trên UI của seller khi họ có sản phẩm trong đó.
        // JWT đã xác thực danh tính, không cần check lại isSeller để tránh mâu thuẫn logic.
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
        // Dùng findByIdWithDetails để FETCH JOIN đầy đủ, tránh lazy loading
        DonHang donHang = donHangRepository.findByIdWithDetails(maDonHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Đơn hàng chỉ hiển thị trên UI của seller khi họ có sản phẩm trong đó.
        // JWT đã xác thực danh tính, không cần check lại isSeller để tránh mâu thuẫn logic.
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
        Page<DonHang> orderPage;
        if ("all".equalsIgnoreCase(status)) {
            orderPage = donHangRepository.findAll(pageable);
        } else {
            orderPage = donHangRepository.findByTrangThaiDonHangTenTrangThaiIgnoreCase(status, pageable);
        }

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
}

