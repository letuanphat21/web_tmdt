package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.DonHangRepository;
import com.group2.web_tmdt.dao.ProductRepository;
import com.group2.web_tmdt.dao.ReviewRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.ReviewDTO;
import com.group2.web_tmdt.dto.TaoReviewRequest;
import com.group2.web_tmdt.entity.DonHang;
import com.group2.web_tmdt.entity.Product;
import com.group2.web_tmdt.entity.Review;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DonHangRepository donHangRepository;

    @Override
    @Transactional
    public ReviewDTO taoReview(String email, TaoReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Product product = productRepository.findById(request.getMaSanPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Dùng query có FETCH JOIN để tránh lazy load chiTietDonHangs
        List<DonHang> donHangs = donHangRepository
                .findByUserWithDetails(user.getMaNguoiDung());

        boolean daMua = donHangs.stream()
                .filter(dh -> dh.getTrangThaiDonHang() != null
                        && ("Đã duyệt".equals(dh.getTrangThaiDonHang().getTenTrangThai())
                            || "Thành công".equals(dh.getTrangThaiDonHang().getTenTrangThai())))
                .flatMap(dh -> dh.getChiTietDonHangs().stream())
                .anyMatch(ct -> ct.getProduct().getMaSanPham() == request.getMaSanPham());

        if (!daMua) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm khi đơn hàng đã được duyệt");
        }

        // Kiểm tra đã đánh giá chưa
        if (reviewRepository.existsByProductMaSanPhamAndUserMaNguoiDung(
                request.getMaSanPham(), user.getMaNguoiDung())) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }

        // Validate điểm
        if (request.getDiemXepHang() < 1 || request.getDiemXepHang() > 5) {
            throw new RuntimeException("Điểm đánh giá phải từ 1 đến 5");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setDiemXepHang(request.getDiemXepHang());
        review.setNhanXet(request.getNhanXet());
        review = reviewRepository.save(review);

        // Map sang DTO
        ReviewDTO dto = new ReviewDTO();
        dto.setMaDanhGia(review.getMaDanhGia());
        dto.setDiemXepHang(review.getDiemXepHang());
        dto.setNhanXet(review.getNhanXet());
        dto.setEmailNguoiDung(user.getEmail());
        String ten = (user.getHoDem() != null ? user.getHoDem() : "")
                + " " + (user.getTen() != null ? user.getTen() : "");
        dto.setTenNguoiDung(ten.trim());
        dto.setAvatarNguoiDung(user.getAvatar());
        return dto;
    }

    @Override
    public boolean daDanhGia(String email, long maSanPham) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return reviewRepository.existsByProductMaSanPhamAndUserMaNguoiDung(
                maSanPham, user.getMaNguoiDung());
    }

    @Override
    public boolean coTheDanhGia(String email, long maSanPham) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<DonHang> donHangs = donHangRepository.findByUserWithDetails(user.getMaNguoiDung());

        return donHangs.stream()
                .filter(dh -> dh.getTrangThaiDonHang() != null
                        && ("Đã duyệt".equals(dh.getTrangThaiDonHang().getTenTrangThai())
                            || "Thành công".equals(dh.getTrangThaiDonHang().getTenTrangThai())))
                .flatMap(dh -> dh.getChiTietDonHangs().stream())
                .anyMatch(ct -> ct.getProduct().getMaSanPham() == maSanPham);
    }
}
