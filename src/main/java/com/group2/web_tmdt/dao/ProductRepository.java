package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lấy tất cả sản phẩm theo danh mục
     */
    List<Product> findByCategoryMaTheLoai(int maTheLoai);

    /**
     * Lấy sản phẩm theo người dùng (người bán)
     */
    List<Product> findByUserMaNguoiDung(long maNguoiDung);

    /**
     * Lấy sản phẩm bán chạy nhất
     */
    @Query(nativeQuery = true, 
           value = "SELECT p.* FROM product p " +
                   "LEFT JOIN chi_tiet_don_hang ctdh ON p.ma_san_pham = ctdh.ma_san_pham " +
                   "GROUP BY p.ma_san_pham " +
                   "ORDER BY SUM(COALESCE(ctdh.so_luong, 0)) DESC")
    Page<Product> findBestSellingProducts(Pageable pageable);
}
