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
            "AND d.trangThai = :trangThaiThanhCong " +
            "GROUP BY DAY(d.ngayTao) " +
            "ORDER BY DAY(d.ngayTao) ASC")
    List<DoanhThuNgayDTO> thongKeDoanhThuTheoThang(
            @Param("maSeller") int maSeller,
            @Param("nam") int nam,
            @Param("thang") int thang,
            @Param("trangThaiThanhCong") String trangThaiThanhCong
    );

    @Query("SELECT new com.group2.web_tmdt.dto.DoanhThuDanhMucDTO(c.tenTheLoai, SUM(ct.soLuong * ct.giaBan)) " +
            "FROM ChiTietDonHang ct " +
            "JOIN ct.product p " +
            "JOIN p.category c " +
            "JOIN ct.donHang dh " +
            "WHERE p.user.maNguoiDung = :maSeller " +
            "AND MONTH(dh.ngayTao) = :thang " +
            "AND YEAR(dh.ngayTao) = :nam " +
            "AND dh.trangThai = 'Hoàn thành' " + // Đã sửa lại cho đồng bộ với hàm trên
            "GROUP BY c.tenTheLoai")
    List<DoanhThuDanhMucDTO> thongKeDoanhThuTheoDanhMuc(
            @Param("maSeller") int maSeller, // Đã đổi Long thành int
            @Param("thang") int thang,
            @Param("nam") int nam
    );
}