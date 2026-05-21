package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    List<DonHang> findByUserMaNguoiDungOrderByNgayTaoDesc(long maNguoiDung);
}
