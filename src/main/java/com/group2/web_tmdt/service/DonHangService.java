package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.dto.PageResponse;

import java.util.List;

public interface DonHangService {

    List<DonHangDTO> taoDoHang(String email, String diaChiNhanHang,
                                double chiPhiGiaoHang, String phuongThucThanhToan);

    List<DonHangDTO> getDonHangCuaUser(String email);

    DonHangDTO getDonHangById(String email, int maDonHang);

    DonHangDTO huyDonHang(String email, int maDonHang, String lyDoHuy);

    List<DonHangDTO> getSellOrdersOfSeller(String email, String trangThai);

    DonHangDTO xacNhanDonHang(String sellerEmail, int maDonHang);

    DonHangDTO huyDonHangBySeller(String sellerEmail, int maDonHang, String lyDoHuy);

    PageResponse<DonHangDTO> getAllOrdersForAdmin(String status, int page, int size);

    DonHangDTO adminConfirmOrder(int maDonHang);

    DonHangDTO adminCancelOrder(int maDonHang, String lyDoHuy);

    /** Admin: Cập nhật trạng thái đơn hàng sang trạng thái mới bất kỳ */
    DonHangDTO updateOrderStatus(int maDonHang, String trangThaiMoi);
}
