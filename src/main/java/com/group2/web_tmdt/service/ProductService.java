package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.*;
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

    /**
     * Tìm kiếm sản phẩm với bộ lọc kết hợp
     */
    Page<ProductDTO> searchProducts(String keyword, Integer categoryId, Integer statusId, 
                                    Double minPrice, Double maxPrice, Pageable pageable);


    void postProduct(ProductForSaleRequest request,String email);


    void approveProduct(Long id);

    void rejectProduct(Long id, RejectProductRequest request);

    Page<ProductPendingDTO>  getPendingProducts(Pageable pageable);

    Page<ProductSellerDTO> getProductsByUser(String email,Pageable pageable);

    void activeProduct(Long id);

    void deactiveProduct(Long id);
}
