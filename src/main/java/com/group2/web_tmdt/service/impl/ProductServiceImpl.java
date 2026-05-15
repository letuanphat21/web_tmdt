package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ProductRepository;
import com.group2.web_tmdt.dto.ProductDTO;
import com.group2.web_tmdt.entity.Product;
import com.group2.web_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
            String tenNguoiBan = product.getUser().getHoDem() + " " + product.getUser().getTen();
            dto.setTenNguoiBan(tenNguoiBan.trim());
            dto.setHinhAnhDaiDien(product.getUser().getAvatar());
        }

        // Tính trung bình đánh giá
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double avgRating = product.getReviews().stream()
                    .mapToDouble(review -> review.getDiemXepHang())
                    .average()
                    .orElse(0.0);
            dto.setDanhGia(avgRating);
        } else {
            dto.setDanhGia(0.0);
        }

        return dto;
    }
}
