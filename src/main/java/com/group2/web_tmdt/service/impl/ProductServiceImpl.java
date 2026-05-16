package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ProductRepository;
import com.group2.web_tmdt.dto.ProductDTO;
import com.group2.web_tmdt.dto.ReviewDTO;
import com.group2.web_tmdt.entity.HinhAnh;
import com.group2.web_tmdt.entity.Product;
import com.group2.web_tmdt.entity.Review;
import com.group2.web_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getNewestProducts(int limit) {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getMaSanPham(), p1.getMaSanPham()))
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getBestSellingProducts(Pageable pageable) {
        return productRepository.findBestSellingProducts(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public ProductDTO getProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryMaTheLoai(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsBySeller(long sellerId) {
        return productRepository.findByUserMaNguoiDung(sellerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setMaSanPham(product.getMaSanPham());
        dto.setTenSanPham(product.getTenSanPham());
        dto.setSoLuong(product.getSoLuong());
        dto.setGiaSanPham(product.getGiaSanPham());

        if (product.getCategory() != null) {
            dto.setTenTheLoai(product.getCategory().getTenTheLoai());
            dto.setMaTheLoai(product.getCategory().getMaTheLoai());
        }

        if (product.getUser() != null) {
            dto.setEmail(product.getUser().getEmail());
            dto.setMaNguoiBan(product.getUser().getMaNguoiDung());
            String tenNguoiBan = (product.getUser().getHoDem() != null ? product.getUser().getHoDem() : "")
                    + " " + (product.getUser().getTen() != null ? product.getUser().getTen() : "");
            dto.setTenNguoiBan(tenNguoiBan.trim());
            dto.setHinhAnhDaiDien(product.getUser().getAvatar());
        }

        // Tính trung bình đánh giá
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double avgRating = product.getReviews().stream()
                    .mapToDouble(Review::getDiemXepHang)
                    .average()
                    .orElse(0.0);
            dto.setDanhGia(avgRating);
            dto.setSoLuongDanhGia(product.getReviews().size());

            // Map danh sách đánh giá chi tiết
            List<ReviewDTO> reviewDTOs = product.getReviews().stream()
                    .map(review -> {
                        ReviewDTO r = new ReviewDTO();
                        r.setMaDanhGia(review.getMaDanhGia());
                        r.setDiemXepHang(review.getDiemXepHang());
                        r.setNhanXet(review.getNhanXet());
                        if (review.getUser() != null) {
                            r.setEmailNguoiDung(review.getUser().getEmail());
                            String ten = (review.getUser().getHoDem() != null ? review.getUser().getHoDem() : "")
                                    + " " + (review.getUser().getTen() != null ? review.getUser().getTen() : "");
                            r.setTenNguoiDung(ten.trim());
                            r.setAvatarNguoiDung(review.getUser().getAvatar());
                        }
                        return r;
                    })
                    .collect(Collectors.toList());
            dto.setDanhGias(reviewDTOs);
        } else {
            dto.setDanhGia(0.0);
            dto.setSoLuongDanhGia(0);
            dto.setDanhGias(Collections.emptyList());
        }

        // Map danh sách hình ảnh sản phẩm
        if (product.getHinhAnhs() != null && !product.getHinhAnhs().isEmpty()) {
            List<String> hinhAnhs = product.getHinhAnhs().stream()
                    .map(HinhAnh::getDuLieuAnh)
                    .collect(Collectors.toList());
            dto.setHinhAnhs(hinhAnhs);
        } else {
            dto.setHinhAnhs(Collections.emptyList());
        }

        return dto;
    }
}
