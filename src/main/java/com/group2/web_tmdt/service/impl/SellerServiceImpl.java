package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.SellerDTO;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final UserRepository userRepository;

    @Override
    public SellerDTO getSellerById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<SellerDTO> getTopRatedSellers(int limit) {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .filter(s -> s.getSoSanPham() > 0)
                .sorted((s1, s2) -> Double.compare(s2.getDanhGiaXepHang(), s1.getDanhGiaXepHang()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerDTO> getTopSellersByProductCount(int limit) {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .filter(s -> s.getSoSanPham() > 0)
                .sorted((s1, s2) -> Long.compare(s2.getSoSanPham(), s1.getSoSanPham()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private SellerDTO convertToDTO(User user) {
        SellerDTO dto = new SellerDTO();
        dto.setMaNguoiDung(user.getMaNguoiDung());
        dto.setEmail(user.getEmail());
        String hoTen = user.getHoDem() + " " + user.getTen();
        dto.setHoTen(hoTen.trim());
        dto.setSoDienThoai(user.getSoDienThoai());
        dto.setDiaChi(user.getDiaChi());
        dto.setAvatar(user.getAvatar());

        // Tính số sản phẩm active=1 và ma_trang_thai=2
        if (user.getProducts() != null) {
            long activeProducts = user.getProducts().stream()
                    .filter(p -> p.isActive() && p.getTrangThaiSanPham() != null && p.getTrangThaiSanPham().getId() == 2)
                    .count();
            dto.setSoSanPham(activeProducts);
        } else {
            dto.setSoSanPham(0);
        }

        // Tính xếp hạng người bán (dựa trên đánh giá của các sản phẩm active=1 và ma_trang_thai=2)
        double avgRating = user.getProducts() != null ? 
                user.getProducts().stream()
                    .filter(p -> p.isActive() && p.getTrangThaiSanPham() != null && p.getTrangThaiSanPham().getId() == 2)
                    .filter(p -> p.getReviews() != null && !p.getReviews().isEmpty())
                    .flatMapToDouble(p -> p.getReviews().stream()
                            .mapToDouble(r -> r.getDiemXepHang()))
                    .average()
                    .orElse(0.0)
                : 0.0;
        dto.setDanhGiaXepHang(avgRating);

        return dto;
    }
}
