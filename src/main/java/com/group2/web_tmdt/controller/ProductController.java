package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.*;
import com.group2.web_tmdt.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

        private final ProductService productService;

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<Page<ProductDTO>>> searchProducts(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer categoryId,
                        @RequestParam(required = false) Integer statusId,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        Pageable pageable) {

                Page<ProductDTO> products = productService.searchProducts(
                                keyword,
                                categoryId,
                                statusId,
                                minPrice,
                                maxPrice,
                                pageable);

                return ApiResponse.ok(
                                "Lấy danh sách sản phẩm thành công!",
                                products);
        }

        @PostMapping("/post")
        public ResponseEntity<ApiResponse<Void>> dangSanPham(
                        @Valid @RequestBody ProductForSaleRequest request,
                        Authentication authentication
                        ) {

                String email = authentication.getName();

                productService.postProduct(request,email);

                return ApiResponse.ok(
                                "Đăng bán sản phẩm thành công, hãy chờ admin duyệt nha");
        }

    @PutMapping("/{productId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveProduct(
            @PathVariable Long productId
    ) {

        productService.approveProduct(productId);

        return ApiResponse.ok("Duyệt sản phẩm thành công");
    }

    @PutMapping("/{productId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectProduct(
            @PathVariable Long productId,
            @Valid @RequestBody RejectProductRequest request
    ) {

        productService.rejectProduct(productId, request);

        return ApiResponse.ok("Đã từ chối sản phẩm");
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<ProductPendingDTO>>> getPendingProducts(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductPendingDTO> result =
                productService.getPendingProducts(pageable);

        return ApiResponse.ok("Lấy danh sách thành công",result);
    }

    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<Page<ProductSellerDTO>>> getProductsAllForSeller(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "ALL") SellerListingFilter filter,
            Authentication authentication) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSellerDTO> products =
                productService.getProductsByUser(email, filter, pageable);

        return ApiResponse.ok("Lấy danh sách thành công", products);
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ProductAdminDTO>>> getProductsAllForAdmin(@RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "6") int size,
                                                                                     @RequestParam(defaultValue = "ALL") SellerListingFilter filter
                                                                                     ){
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductAdminDTO> products = productService.getProductsForAdmin(pageable,filter);

        return ApiResponse.ok("Lấy danh sách thành công", products);
    }




    @PutMapping("/{productId}/active")
    public ResponseEntity<ApiResponse<Void>> activeProduct(
            @PathVariable Long productId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        productService.activeProduct(productId, authentication.getName(), isAdmin);
        return ApiResponse.ok("Đã active sản phẩm thành công");
    }

    @PutMapping("/{productId}/deactive")
    public ResponseEntity<ApiResponse<Void>> deactiveProduct(
            @PathVariable Long productId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        productService.deactiveProduct(productId, authentication.getName(), isAdmin);
        return ApiResponse.ok("Đã deactive sản phẩm thành công");
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        productService.updateProduct(productId, request, authentication.getName(), isAdmin);

        return ApiResponse.ok("Cập nhật sản phẩm thành công");
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductSellerDTO>> getProduct(
            @PathVariable Long productId,
            Authentication authentication
    ){
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        ProductSellerDTO product = productService.getProductForManagement(
                productId,
                isAdmin
        );

        return ApiResponse.ok("Lấy sản phẩm thành công", product);
    }

}