package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.ReviewDTO;
import com.group2.web_tmdt.dto.TaoReviewRequest;

public interface ReviewService {

    /** Tạo đánh giá sản phẩm (user phải đã mua sản phẩm đó) */
    ReviewDTO taoReview(String email, TaoReviewRequest request);

    /** Kiểm tra user đã đánh giá sản phẩm này chưa */
    boolean daDanhGia(String email, long maSanPham);
}
