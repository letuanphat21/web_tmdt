package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByProductMaSanPhamAndUserMaNguoiDung(long maSanPham, long maNguoiDung);

    List<Review> findByProductMaSanPham(long maSanPham);

    long countByProductMaSanPham(long maSanPham);

    @Query("SELECT COALESCE(AVG(r.diemXepHang), 0) FROM Review r WHERE r.product.maSanPham = :maSanPham")
    double avgDiemByProductMaSanPham(@Param("maSanPham") long maSanPham);
}
