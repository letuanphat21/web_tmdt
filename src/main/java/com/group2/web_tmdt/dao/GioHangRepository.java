package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    Optional<GioHang> findByUserMaNguoiDung(long maNguoiDung);
}
