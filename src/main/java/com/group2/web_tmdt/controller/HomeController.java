package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.CategoryDTO;
import com.group2.web_tmdt.dto.ProductDTO;
import com.group2.web_tmdt.dto.SellerDTO;
import com.group2.web_tmdt.service.CategoryService;
import com.group2.web_tmdt.service.ProductService;
import com.group2.web_tmdt.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SellerService sellerService;

    /**
     * GET /api/home/hero
     * Lấy dữ liệu cho Hero section
     */
    @GetMapping("/hero")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHeroData() {
        Map<String, Object> heroData = new HashMap<>();
        
        // Có thể bổ sung thêm dữ liệu banner, slide show, etc.
        heroData.put("title", "Chào mừng đến cửa hàng trực tuyến");
        heroData.put("description", "Khám phá hàng ngàn sản phẩm chất lượng cao");
        
        return ApiResponse.ok("Lấy dữ liệu Hero thành công!", heroData);
    }

    /**
     * GET /api/home/categories
     * Lấy danh sách tất cả danh mục
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.ok("Lấy danh sách danh mục thành công!", categories);
    }

    /**
     * GET /api/home/products/newest
     * Lấy sản phẩm mới đăng
     * 
     * Query params:
     * - limit: số lượng sản phẩm (mặc định: 10)
     */
    @GetMapping("/products/newest")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getNewestProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String excludeEmail) {
        String finalExcludeEmail = (excludeEmail != null && !excludeEmail.isBlank())
                ? excludeEmail
                : (userDetails != null ? userDetails.getUsername() : null);
        List<ProductDTO> products = productService.getNewestProducts(limit, finalExcludeEmail);
        return ApiResponse.ok("Lấy sản phẩm mới đăng thành công!", products);
    }

    /**
     * GET /api/home/products/best-selling
     * Lấy sản phẩm bán chạy nhất
     * 
     * Query params:
     * - page: trang (mặc định: 0)
     * - size: số lượng sản phẩm trên mỗi trang (mặc định: 10)
     */
    @GetMapping("/products/best-selling")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getBestSellingProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String excludeEmail) {
        String finalExcludeEmail = (excludeEmail != null && !excludeEmail.isBlank())
                ? excludeEmail
                : (userDetails != null ? userDetails.getUsername() : null);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getBestSellingProducts(pageable, finalExcludeEmail);
        return ApiResponse.ok("Lấy sản phẩm bán chạy nhất thành công!", products);
    }

    /**
     * GET /api/home/sellers/top-rated
     * Lấy danh sách top người bán có xếp hạng cao nhất
     * 
     * Query params:
     * - limit: số lượng người bán (mặc định: 8)
     */
    @GetMapping("/sellers/top-rated")
    public ResponseEntity<ApiResponse<List<SellerDTO>>> getTopRatedSellers(
            @RequestParam(defaultValue = "8") int limit) {
        List<SellerDTO> sellers = sellerService.getTopRatedSellers(limit);
        return ApiResponse.ok("Lấy danh sách top người bán thành công!", sellers);
    }

    /**
     * GET /api/home/sellers/top-products
     * Lấy danh sách top người bán có sản phẩm nhiều nhất
     * 
     * Query params:
     * - limit: số lượng người bán (mặc định: 8)
     */
    @GetMapping("/sellers/top-products")
    public ResponseEntity<ApiResponse<List<SellerDTO>>> getTopSellersByProductCount(
            @RequestParam(defaultValue = "8") int limit) {
        List<SellerDTO> sellers = sellerService.getTopSellersByProductCount(limit);
        return ApiResponse.ok("Lấy danh sách người bán top sản phẩm thành công!", sellers);
    }

    /**
     * GET /api/home/products/category/{categoryId}
     * Lấy sản phẩm theo danh mục
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(
            @PathVariable int categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ApiResponse.ok("Lấy sản phẩm theo danh mục thành công!", products);
    }

    /**
     * GET /api/home/products/{id}
     * Lấy chi tiết một sản phẩm
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductDetail(@PathVariable long id) {
        ProductDTO product = productService.getProductById(id);
        if (product != null) {
            return ApiResponse.ok("Lấy chi tiết sản phẩm thành công!", product);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại.");
        }
    }

    /**
     * GET /api/home/sellers/{id}
     * Lấy thông tin chi tiết người bán
     */
    @GetMapping("/sellers/{id}")
    public ResponseEntity<ApiResponse<SellerDTO>> getSellerDetail(@PathVariable long id) {
        SellerDTO seller = sellerService.getSellerById(id);
        if (seller != null) {
            return ApiResponse.ok("Lấy thông tin người bán thành công!", seller);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Người bán không tồn tại.");
        }
    }
}
