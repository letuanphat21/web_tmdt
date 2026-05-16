package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.GioHangItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GioHangItemRepository extends JpaRepository<GioHangItem, Long> {

    Optional<GioHangItem> findByGioHangMaGioHangAndProductMaSanPham(long maGioHang, long maSanPham);

    @Query("SELECT i FROM GioHangItem i JOIN FETCH i.product p LEFT JOIN FETCH p.hinhAnhs LEFT JOIN FETCH p.user WHERE i.gioHang.maGioHang = :maGioHang")
    List<GioHangItem> findAllByGioHangId(@Param("maGioHang") long maGioHang);
}
