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

    @GetMapping("/hero")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHeroData() {
        Map<String, Object> heroData = new HashMap<>();
        
        // Có thể bổ sung thêm dữ liệu banner, slide show, etc.
        heroData.put("title", "Chào mừng đến cửa hàng trực tuyến");
        heroData.put("description", "Khám phá hàng ngàn sản phẩm chất lượng cao");
        
        return ApiResponse.ok("Lấy dữ liệu Hero thành công!", heroData);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories().stream()
                .filter(c -> c.getActive() == null || Boolean.TRUE.equals(c.getActive()))
                .collect(java.util.stream.Collectors.toList());
        return ApiResponse.ok("Lấy danh sách danh mục thành công!", categories);
    }

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

    @GetMapping("/sellers/top-rated")
    public ResponseEntity<ApiResponse<List<SellerDTO>>> getTopRatedSellers(
            @RequestParam(defaultValue = "8") int limit) {
        List<SellerDTO> sellers = sellerService.getTopRatedSellers(limit);
        return ApiResponse.ok("Lấy danh sách top người bán thành công!", sellers);
    }

    @GetMapping("/sellers/top-products")
    public ResponseEntity<ApiResponse<List<SellerDTO>>> getTopSellersByProductCount(
            @RequestParam(defaultValue = "8") int limit) {
        List<SellerDTO> sellers = sellerService.getTopSellersByProductCount(limit);
        return ApiResponse.ok("Lấy danh sách người bán top sản phẩm thành công!", sellers);
    }

    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(
            @PathVariable int categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ApiResponse.ok("Lấy sản phẩm theo danh mục thành công!", products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductDetail(@PathVariable long id) {
        ProductDTO product = productService.getProductById(id);
        if (product != null) {
            return ApiResponse.ok("Lấy chi tiết sản phẩm thành công!", product);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại.");
        }
    }

    @GetMapping("/sellers/{id}")
    public ResponseEntity<ApiResponse<SellerDTO>> getSellerDetail(@PathVariable long id) {
        SellerDTO seller = sellerService.getSellerById(id);
        if (seller != null) {
            return ApiResponse.ok("Lấy thông tin người bán thành công!", seller);
        } else {
            return ApiResponse.error(HttpStatus.NOT_FOUND, "Người bán không tồn tại.");
        }
    }

    @GetMapping("/sellers/{id}/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsBySeller(
            @PathVariable long id,
            @RequestParam(defaultValue = "8") int limit,
            @RequestParam(required = false) Long excludeProductId) {
        List<ProductDTO> products = productService.getProductsBySeller(id);
        // Loại trừ sản phẩm đang xem
        if (excludeProductId != null) {
            products = products.stream()
                    .filter(p -> p.getMaSanPham() != excludeProductId)
                    .collect(java.util.stream.Collectors.toList());
        }
        // Giới hạn số lượng
        if (products.size() > limit) {
            products = products.subList(0, limit);
        }
        return ApiResponse.ok("Lấy sản phẩm của người bán thành công!", products);
    }
}
