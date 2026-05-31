package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * Lấy danh sách sản phẩm mới nhất, loại trừ sản phẩm của người bán đang đăng nhập (nếu có)
     */
    List<ProductDTO> getNewestProducts(int limit, String excludeEmail);

    /**
     * Lấy danh sách sản phẩm bán chạy nhất, loại trừ sản phẩm của người bán đang đăng nhập (nếu có)
     */
    Page<ProductDTO> getBestSellingProducts(Pageable pageable, String excludeEmail);

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
     * Tìm kiếm sản phẩm với bộ lọc kết hợp (không bao gồm sản phẩm của người dùng hiện tại)
     */
    Page<ProductDTO> searchProducts(String keyword, Integer categoryId, Integer statusId, 
                                    Double minPrice, Double maxPrice, Long currentUserId, Pageable pageable);


    void postProduct(ProductForSaleRequest request,String email);


    void approveProduct(Long id);

    void rejectProduct(Long id, RejectProductRequest request);

    Page<ProductPendingDTO>  getPendingProducts(Pageable pageable);

    Page<ProductSellerDTO> getProductsByUser(String email, SellerListingFilter filter, Pageable pageable);

    void activeProduct(Long id, String email, boolean isAdmin);

    void deactiveProduct(Long id, String email, boolean isAdmin);

    void updateProduct(Long productId, ProductUpdateRequest request, String email, boolean isAdmin);

    ProductSellerDTO getProductForManagement(long productId, boolean isAdmin);

    Page<ProductAdminDTO> getProductsForAdmin(Pageable pageable,SellerListingFilter filter);
}
