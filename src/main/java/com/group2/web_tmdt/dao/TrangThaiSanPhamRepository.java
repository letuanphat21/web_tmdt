package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.TinhTrang;
import com.group2.web_tmdt.entity.TrangThaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrangThaiSanPhamRepository extends JpaRepository<TrangThaiSanPham, Integer> {

    Optional<TrangThaiSanPham> findByTenTrangThai(String tenTrangThai);
}
