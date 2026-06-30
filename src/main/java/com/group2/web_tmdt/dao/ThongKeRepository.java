package com.group2.web_tmdt.dao;

import com.group2.web_tmdt.dto.DoanhThuDanhMucDTO;
import com.group2.web_tmdt.entity.ChiTietDonHang;
import com.group2.web_tmdt.dto.DoanhThuNgayDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongKeRepository extends JpaRepository<ChiTietDonHang, Long> {

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuNgayDTO(DAY(d.ngayTao), SUM(ct.soLuong * ct.giaBan)) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.donHang d " +
            "JOIN ct.product p " +
            "WHERE p.user.maNguoiDung = :maSeller " +
            "AND YEAR(d.ngayTao) = :nam " +
            "AND MONTH(d.ngayTao) = :thang " +
            "AND d.trangThaiDonHang.id = 5 " + // ✅ Đã sửa: Truy vấn trực tiếp bằng ID = 5
            "GROUP BY DAY(d.ngayTao) " +
            "ORDER BY DAY(d.ngayTao) ASC")
    List<DoanhThuNgayDTO> thongKeDoanhThuTheoThang(
            @Param("maSeller") int maSeller,
            @Param("nam") int nam,
            @Param("thang") int thang
            // ✅ Đã xóa tham số String trangThaiThanhCong để code gọn hơn
    );

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuDanhMucDTO(c.tenTheLoai, SUM(ct.soLuong * ct.giaBan)) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.product p " +
            "JOIN p.category c " +
            "JOIN ct.donHang dh " +
            "WHERE p.user.maNguoiDung = :maSeller " +
            "AND MONTH(dh.ngayTao) = :thang " +
            "AND YEAR(dh.ngayTao) = :nam " +
            "AND dh.trangThaiDonHang.id = 5 " + 
            "GROUP BY c.tenTheLoai")
    List<DoanhThuDanhMucDTO> thongKeDoanhThuTheoDanhMuc(
            @Param("maSeller") int maSeller, 
            @Param("thang") int thang,
            @Param("nam") int nam
    );

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuNgayDTO(DAY(d.ngayTao), SUM(ct.soLuong * ct.giaBan)) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.donHang d " +
            "JOIN ct.product p " +
            "WHERE p.user.maNguoiDung = :maSeller " +
            "AND d.ngayTao >= :tuNgay " +
            "AND d.ngayTao <= :denNgay " +
            "AND d.trangThaiDonHang.id = 5 " +
            "GROUP BY DAY(d.ngayTao) " +
            "ORDER BY DAY(d.ngayTao) ASC")
    List<DoanhThuNgayDTO> thongKeDoanhThuTheoKhoangNgay(
            @Param("maSeller") int maSeller,
            @Param("tuNgay") java.time.LocalDate tuNgay,
            @Param("denNgay") java.time.LocalDate denNgay
    );

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuDanhMucDTO(c.tenTheLoai, SUM(ct.soLuong * ct.giaBan)) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.product p " +
            "JOIN p.category c " +
            "JOIN ct.donHang dh " +
            "WHERE p.user.maNguoiDung = :maSeller " +
            "AND dh.ngayTao >= :tuNgay " +
            "AND dh.ngayTao <= :denNgay " +
            "AND dh.trangThaiDonHang.id = 5 " +
            "GROUP BY c.tenTheLoai")
    List<DoanhThuDanhMucDTO> thongKeDoanhThuTheoDanhMucKhoangNgay(
            @Param("maSeller") int maSeller,
            @Param("tuNgay") java.time.LocalDate tuNgay,
            @Param("denNgay") java.time.LocalDate denNgay
    );

    @Query("SELECT COALESCE(SUM(dh.tongTien), 0.0) FROM DonHang dh WHERE dh.trangThaiDonHang.id = 5")
    double countTongDoanhThu();

    @Query("SELECT COUNT(dh) FROM DonHang dh")
    long countTongDonHang();

    @Query("SELECT COUNT(u) FROM User u")
    long countTongKhachHang();

    @Query("SELECT COUNT(DISTINCT p.user) FROM Product p")
    long countTongCuaHang();

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuThangDTO(MONTH(dh.ngayTao), SUM(dh.tongTien)) " +
           "FROM DonHang dh " +
           "WHERE dh.trangThaiDonHang.id = 5 " +
           "AND YEAR(dh.ngayTao) = :nam " +
           "GROUP BY MONTH(dh.ngayTao) " +
           "ORDER BY MONTH(dh.ngayTao) ASC")
    List<com.group2.web_tmdt.dto.DoanhThuThangDTO> thongKeDoanhThuNamTheoThang(@Param("nam") int nam);

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuDanhMucDTO(c.tenTheLoai, SUM(ct.soLuong * ct.giaBan)) " +
           "FROM ChiTietDonHang ct " +
           "JOIN ct.product p " +
           "JOIN p.category c " +
           "JOIN ct.donHang dh " +
           "WHERE dh.trangThaiDonHang.id = 5 " +
           "AND YEAR(dh.ngayTao) = :nam " +
           "GROUP BY c.tenTheLoai")
    List<DoanhThuDanhMucDTO> thongKeDoanhThuNamTheoDanhMuc(@Param("nam") int nam);
}