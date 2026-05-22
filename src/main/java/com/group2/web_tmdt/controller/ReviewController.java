package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.ReviewDTO;
import com.group2.web_tmdt.dto.TaoReviewRequest;
import com.group2.web_tmdt.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews
     * Tạo đánh giá sản phẩm
     * Body: { "maSanPham": 1, "diemXepHang": 5, "nhanXet": "..." }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> taoReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaoReviewRequest request) {
        ReviewDTO review = reviewService.taoReview(userDetails.getUsername(), request);
        return ApiResponse.ok("Đánh giá thành công!", review);
    }

    /**
     * GET /api/reviews/check?maSanPham=1
     * Kiểm tra user đã đánh giá sản phẩm này chưa
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> kiemTraDaDanhGia(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam long maSanPham) {
        boolean daDanhGia = reviewService.daDanhGia(userDetails.getUsername(), maSanPham);
        return ApiResponse.ok("OK", Map.of("daDanhGia", daDanhGia));
    }
}
