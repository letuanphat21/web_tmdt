package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrangThaiDonHangRepository extends JpaRepository<TrangThaiDonHang, Integer> {
    Optional<TrangThaiDonHang> findByTenTrangThai(String tenTrangThai);
}
