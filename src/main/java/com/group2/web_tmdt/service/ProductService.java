package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * Lấy danh sách sản phẩm mới nhất
     */
    List<ProductDTO> getNewestProducts(int limit);

    /**
     * Lấy danh sách sản phẩm bán chạy nhất
     */
    Page<ProductDTO> getBestSellingProducts(Pageable pageable);

    /**
     * Lấy sản phẩm theo ID
     */
    ProductDTO getProductById(long id);

    /**
     * Lấy sản phẩm theo danh mục
     */
    List<ProductDTO> getProductsByCategory(int categoryId);

    /**
     * Lấy sản phẩm của người bán
     */
    List<ProductDTO> getProductsBySeller(long sellerId);
}
