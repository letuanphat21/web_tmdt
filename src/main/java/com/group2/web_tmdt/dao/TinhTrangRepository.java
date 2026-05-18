package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.TinhTrang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TinhTrangRepository extends JpaRepository<TinhTrang, Integer> {

    /**
     * Tìm tình trạng theo tên
     */
    Optional<TinhTrang> findByTenTinhTrang(String tenTinhTrang);

    /**
     * Kiểm tra tình trạng có tồn tại theo tên
     */
    boolean existsByTenTinhTrang(String tenTinhTrang);
}
