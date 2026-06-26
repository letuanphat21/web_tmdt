package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.entity.Product;
import com.group2.web_tmdt.entity.TrangThaiSanPham;
import com.group2.web_tmdt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lấy tất cả sản phẩm theo danh mục (chỉ lấy sản phẩm active=1 và ma_trang_thai=2)
     */
    @Query("SELECT p FROM Product p WHERE p.category.maTheLoai = :maTheLoai AND p.active = true AND p.trangThaiSanPham.id = 2")
    List<Product> findByCategoryMaTheLoai(@Param("maTheLoai") int maTheLoai);

    /**
     * Lấy sản phẩm theo người dùng (người bán) - chỉ lấy sản phẩm active=1 và ma_trang_thai=2
     */
    @Query("SELECT p FROM Product p WHERE p.user.maNguoiDung = :maNguoiDung AND p.active = true AND p.trangThaiSanPham.id = 2")
    List<Product> findByUserMaNguoiDung(@Param("maNguoiDung") long maNguoiDung);

    /**
     * Lấy sản phẩm bán chạy nhất, loại trừ sản phẩm của người bán đang đăng nhập (nếu có)
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.active = true AND p.trangThaiSanPham.id = 2 " +
           "AND (:excludeEmail IS NULL OR p.user.email != :excludeEmail) " +
           "ORDER BY p.soLuongDaBan DESC")
    Page<Product> findBestSellingProducts(@Param("excludeEmail") String excludeEmail, Pageable pageable);

    /**
     * Lấy danh sách sản phẩm mới nhất, loại trừ sản phẩm của người bán đang đăng nhập (nếu có)
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.active = true AND p.trangThaiSanPham.id = 2 " +
           "AND (:excludeEmail IS NULL OR p.user.email != :excludeEmail) " +
           "ORDER BY p.maSanPham DESC")
    List<Product> findNewestProducts(@Param("excludeEmail") String excludeEmail, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm theo tên (chỉ lấy sản phẩm active=1 và ma_trang_thai=2)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :tenSanPham, '%')) AND p.active = true AND p.trangThaiSanPham.id = 2")
    Page<Product> findByTenSanPhamContainingIgnoreCase(@Param("tenSanPham") String tenSanPham, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm theo danh mục (chỉ lấy sản phẩm active=1 và ma_trang_thai=2)
     */
    @Query("SELECT p FROM Product p WHERE p.category.maTheLoai = :maTheLoai AND p.active = true AND p.trangThaiSanPham.id = 2")
    Page<Product> findByCategoryMaTheLoai(@Param("maTheLoai") int maTheLoai, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm theo tình trạng (chỉ lấy sản phẩm active=1 và ma_trang_thai=2)
     */
    @Query("SELECT p FROM Product p WHERE p.tinhTrang.maTinhTrang = :maTinhTrang AND p.active = true AND p.trangThaiSanPham.id = 2")
    Page<Product> findByTinhTrangMaTinhTrang(@Param("maTinhTrang") int maTinhTrang, Pageable pageable);

    /**
     * Tìm kiếm sản phẩm với bộ lọc kết hợp:
     * - Tên sản phẩm
     * - Danh mục
     * - Tình trạng
     * - Khoảng giá
     * Chỉ lấy sản phẩm active=1 và ma_trang_thai=2
     */
    @Query(value = "SELECT p FROM Product p WHERE " +
           "(:tenSanPham IS NULL OR LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :tenSanPham, '%'))) " +
           "AND (:maTheLoai IS NULL OR p.category.maTheLoai = :maTheLoai) " +
           "AND (:maTinhTrang IS NULL OR p.tinhTrang.maTinhTrang = :maTinhTrang) " +
           "AND (:giaMin IS NULL OR p.giaSanPham >= :giaMin) " +
           "AND (:giaMax IS NULL OR p.giaSanPham <= :giaMax) " +
           "AND p.active = true " +
           "AND p.trangThaiSanPham.id = 2 " +
           "AND (:currentUserId IS NULL OR p.user.maNguoiDung != :currentUserId)")
    Page<Product> searchProducts(
            @Param("tenSanPham") String tenSanPham,
            @Param("maTheLoai") Integer maTheLoai,
            @Param("maTinhTrang") Integer maTinhTrang,
            @Param("giaMin") Double giaMin,
            @Param("giaMax") Double giaMax,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable
    );


    Page<Product> findByTrangThaiSanPham(TrangThaiSanPham trangThaiSanPham, Pageable pageable);

    Page<Product> findByUser(User user, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user = :user AND p.trangThaiSanPham.tenTrangThai = 'PENDING'")
    Page<Product> findPendingByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user = :user AND p.trangThaiSanPham.tenTrangThai = 'APPROVED' AND p.active = true AND p.soLuong > 0")
    Page<Product> findActiveListingsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user = :user AND p.trangThaiSanPham.tenTrangThai = 'APPROVED' AND p.soLuong <= 0")
    Page<Product> findSoldOutByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user = :user AND p.trangThaiSanPham.tenTrangThai = 'REJECTED'")
    Page<Product> findRejectListingsByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user = :user AND p.active = false")
    Page<Product> findDeactiveListingsByUser(@Param("user") User user, Pageable pageable);





//    Admin
    @Query("SELECT p FROM Product p WHERE p.trangThaiSanPham.tenTrangThai = 'APPROVED' AND p.active = true AND p.soLuong > 0")
    Page<Product> findProductsActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE  p.trangThaiSanPham.tenTrangThai = 'PENDING'")
    Page<Product> findProductsPending(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE  p.trangThaiSanPham.tenTrangThai = 'REJECTED'")
    Page<Product> findProductsRejected(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.trangThaiSanPham.tenTrangThai = 'APPROVED' AND p.soLuong <= 0")
    Page<Product> findProductSoldOut( Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.trangThaiSanPham.tenTrangThai = 'APPROVED' AND p.active = false")
    Page<Product> findProductsDeactive(Pageable pageable);


}

