package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.ProductRepository;
import com.group2.web_tmdt.dto.ProductDTO;
import com.group2.web_tmdt.entity.Product;
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

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Integer categoryId, Integer statusId, 
                                           Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryId, statusId, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setMaSanPham(product.getMaSanPham());
        dto.setTenSanPham(product.getTenSanPham());
        dto.setGiaSanPham(product.getGiaSanPham());
        dto.setSoLuong(product.getSoLuong());
        dto.setTenNguoiBan(product.getUser().getEmail());
        dto.setMaNguoiBan(product.getUser().getMaNguoiDung());
        dto.setEmail(product.getUser().getEmail());
        
        // Set category info
        if (product.getCategory() != null) {
            dto.setTenTheLoai(product.getCategory().getTenTheLoai());
            dto.setMaTheLoai(product.getCategory().getMaTheLoai());
        }
        
        // Set status info
        if (product.getTinhTrang() != null) {
            dto.setMaTinhTrang(product.getTinhTrang().getMaTinhTrang());
            dto.setTenTinhTrang(product.getTinhTrang().getTenTinhTrang());
        }

        // Map danh sách hình ảnh sản phẩm
        if (product.getHinhAnhs() != null && !product.getHinhAnhs().isEmpty()) {
            List<String> hinhAnhs = product.getHinhAnhs().stream()
                    .map(hinhAnh -> hinhAnh.getDuongDan())
                    .collect(Collectors.toList());
            dto.setHinhAnhs(hinhAnhs);
            
            // Lấy ảnh đầu tiên của SẢN PHẨM làm ảnh đại diện
            if (!hinhAnhs.isEmpty()) {
                dto.setHinhAnhDaiDien(hinhAnhs.get(0));
            }
        } else {
            dto.setHinhAnhs(Collections.emptyList());
            dto.setHinhAnhDaiDien(null);
        }

        return dto;
    }
}
