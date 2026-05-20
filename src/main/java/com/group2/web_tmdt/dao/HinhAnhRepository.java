package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {

    List<HinhAnh> findByProduct_MaSanPham(Long maSanPham);

    long countByProduct_MaSanPham(Long maSanPham);

    Optional<HinhAnh> findByMaHinhAnhAndProduct_MaSanPham(int maHinhAnh, Long maSanPham);
}
