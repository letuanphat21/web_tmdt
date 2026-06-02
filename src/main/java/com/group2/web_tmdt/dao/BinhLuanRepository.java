package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Long> {

    @Query("SELECT b FROM BinhLuan b WHERE b.product.maSanPham = :maSanPham " +
           "AND b.binhLuanCha IS NULL ORDER BY b.thoiGianTao DESC")
    List<BinhLuan> findBinhLuanGocByProduct(@Param("maSanPham") long maSanPham);

    @Query("SELECT b FROM BinhLuan b WHERE b.binhLuanCha.maBinhLuan = :maBinhLuanCha " +
           "ORDER BY b.thoiGianTao ASC")
    List<BinhLuan> findTraLoiByBinhLuanCha(@Param("maBinhLuanCha") long maBinhLuanCha);
}
