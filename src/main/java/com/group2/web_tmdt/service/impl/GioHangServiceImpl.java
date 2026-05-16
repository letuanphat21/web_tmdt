package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.GioHangItemRepository;
import com.group2.web_tmdt.dao.GioHangRepository;
import com.group2.web_tmdt.dao.ProductRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.GioHangDTO;
import com.group2.web_tmdt.dto.GioHangItemDTO;
import com.group2.web_tmdt.entity.GioHang;
import com.group2.web_tmdt.entity.GioHangItem;
import com.group2.web_tmdt.entity.Product;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.service.GioHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GioHangServiceImpl implements GioHangService {

    private final GioHangRepository gioHangRepository;
    private final GioHangItemRepository gioHangItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public GioHangDTO getGioHang(String email) {
        User user = findUser(email);
        Optional<GioHang> gioHangOpt = gioHangRepository.findByUserMaNguoiDung(user.getMaNguoiDung());
        if (gioHangOpt.isEmpty()) {
            return emptyCartDTO();
        }
        return buildDTO(gioHangOpt.get().getMaGioHang());
    }

    @Override
    @Transactional
    public GioHangDTO themVaoGioHang(String email, long maSanPham, int soLuong) {
        User user = findUser(email);
        Product product = productRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (soLuong <= 0) throw new RuntimeException("Số lượng phải lớn hơn 0");
        if (soLuong > product.getSoLuong()) throw new RuntimeException("Số lượng vượt quá tồn kho");

        GioHang gioHang = gioHangRepository.findByUserMaNguoiDung(user.getMaNguoiDung())
                .orElseGet(() -> {
                    GioHang newCart = new GioHang();
                    newCart.setUser(user);
                    return gioHangRepository.save(newCart);
                });

        Optional<GioHangItem> existingItem = gioHangItemRepository
                .findByGioHangMaGioHangAndProductMaSanPham(gioHang.getMaGioHang(), maSanPham);

        if (existingItem.isPresent()) {
            GioHangItem item = existingItem.get();
            int newQty = item.getSoLuong() + soLuong;
            if (newQty > product.getSoLuong()) throw new RuntimeException("Số lượng vượt quá tồn kho");
            item.setSoLuong(newQty);
            gioHangItemRepository.save(item);
        } else {
            GioHangItem newItem = new GioHangItem();
            newItem.setGioHang(gioHang);
            newItem.setProduct(product);
            newItem.setSoLuong(soLuong);
            gioHangItemRepository.save(newItem);
        }

        return buildDTO(gioHang.getMaGioHang());
    }

    @Override
    @Transactional
    public GioHangDTO xoaKhoiGioHang(String email, long maItem) {
        User user = findUser(email);

        GioHangItem item = gioHangItemRepository.findById(maItem)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item trong giỏ hàng"));

        if (item.getGioHang().getUser().getMaNguoiDung() != user.getMaNguoiDung()) {
            throw new RuntimeException("Không có quyền xóa item này");
        }

        long maGioHang = item.getGioHang().getMaGioHang();
        gioHangItemRepository.deleteById(maItem);
        gioHangItemRepository.flush();

        return buildDTO(maGioHang);
    }

    @Override
    @Transactional
    public GioHangDTO capNhatSoLuong(String email, long maItem, int soLuongMoi) {
        User user = findUser(email);

        GioHangItem item = gioHangItemRepository.findById(maItem)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item trong giỏ hàng"));

        if (item.getGioHang().getUser().getMaNguoiDung() != user.getMaNguoiDung()) {
            throw new RuntimeException("Không có quyền cập nhật item này");
        }

        long maGioHang = item.getGioHang().getMaGioHang();

        if (soLuongMoi <= 0) {
            gioHangItemRepository.deleteById(maItem);
            gioHangItemRepository.flush();
        } else {
            if (soLuongMoi > item.getProduct().getSoLuong())
                throw new RuntimeException("Số lượng vượt quá tồn kho");
            item.setSoLuong(soLuongMoi);
            gioHangItemRepository.save(item);
        }

        return buildDTO(maGioHang);
    }

    @Override
    @Transactional
    public void xoaGioHang(String email) {
        User user = findUser(email);
        gioHangRepository.findByUserMaNguoiDung(user.getMaNguoiDung())
                .ifPresent(gioHang -> {
                    gioHangItemRepository.deleteAll(
                            gioHangItemRepository.findAllByGioHangId(gioHang.getMaGioHang())
                    );
                    gioHangItemRepository.flush();
                });
    }

    // -------------------------------------------------------
    // Helper: query items trực tiếp từ DB, không dùng entity cache
    // -------------------------------------------------------
    private GioHangDTO buildDTO(long maGioHang) {
        List<GioHangItem> items = gioHangItemRepository.findAllByGioHangId(maGioHang);

        List<GioHangItemDTO> itemDTOs = items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());

        GioHangDTO dto = new GioHangDTO();
        dto.setMaGioHang(maGioHang);
        dto.setItems(itemDTOs);
        dto.setTongSoLuong(itemDTOs.stream().mapToInt(GioHangItemDTO::getSoLuong).sum());
        dto.setTongTien(itemDTOs.stream()
                .mapToDouble(i -> i.getGiaSanPham() * i.getSoLuong())
                .sum());
        return dto;
    }

    private GioHangDTO emptyCartDTO() {
        GioHangDTO dto = new GioHangDTO();
        dto.setItems(Collections.emptyList());
        dto.setTongSoLuong(0);
        dto.setTongTien(0);
        return dto;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    private GioHangItemDTO convertItemToDTO(GioHangItem item) {
        GioHangItemDTO dto = new GioHangItemDTO();
        dto.setMaItem(item.getMaItem());
        dto.setSoLuong(item.getSoLuong());

        Product p = item.getProduct();
        dto.setMaSanPham(p.getMaSanPham());
        dto.setTenSanPham(p.getTenSanPham());
        dto.setGiaSanPham(p.getGiaSanPham());
        dto.setSoLuongTonKho(p.getSoLuong());

        if (p.getHinhAnhs() != null && !p.getHinhAnhs().isEmpty()) {
            dto.setHinhAnh(p.getHinhAnhs().get(0).getDuLieuAnh());
        }

        if (p.getUser() != null) {
            String ten = (p.getUser().getHoDem() != null ? p.getUser().getHoDem() : "")
                    + " " + (p.getUser().getTen() != null ? p.getUser().getTen() : "");
            dto.setTenNguoiBan(ten.trim());
        }

        return dto;
    }
}
