package com.group2.web_tmdt.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.group2.web_tmdt.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    Page<DonHang> findByTrangThaiDonHangTenTrangThaiIgnoreCase(String tenTrangThai, Pageable pageable);

    List<DonHang> findByUserMaNguoiDungOrderByNgayTaoDesc(long maNguoiDung);

    @Query("SELECT DISTINCT dh FROM DonHang dh " +
           "LEFT JOIN FETCH dh.chiTietDonHangs ct " +
           "LEFT JOIN FETCH ct.product p " +
           "WHERE dh.user.maNguoiDung = :maNguoiDung " +
           "ORDER BY dh.ngayTao DESC")
    List<DonHang> findByUserWithDetails(@org.springframework.data.repository.query.Param("maNguoiDung") long maNguoiDung);

    @Query("SELECT DISTINCT dh FROM DonHang dh " +
           "LEFT JOIN FETCH dh.chiTietDonHangs ct " +
           "LEFT JOIN FETCH ct.product p " +
           "LEFT JOIN FETCH p.user " +
           "ORDER BY dh.ngayTao DESC")
    List<DonHang> findAllWithDetails();

    @Query("SELECT DISTINCT dh FROM DonHang dh " +
           "LEFT JOIN FETCH dh.chiTietDonHangs ct " +
           "LEFT JOIN FETCH ct.product p " +
           "LEFT JOIN FETCH p.user " +
           "WHERE dh.maDonHang = :maDonHang")
    java.util.Optional<DonHang> findByIdWithDetails(@org.springframework.data.repository.query.Param("maDonHang") int maDonHang);
}

