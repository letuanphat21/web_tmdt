package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByProductMaSanPhamAndUserMaNguoiDung(long maSanPham, long maNguoiDung);
}
