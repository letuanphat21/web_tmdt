package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.dto.PageResponse;

import java.util.List;

public interface DonHangService {

    /** Tạo đơn hàng từ giỏ hàng hiện tại của user */
    DonHangDTO taoDoHang(String email, String diaChiNhanHang, double chiPhiGiaoHang, String phuongThucThanhToan);

    /** Lấy danh sách đơn hàng của user */
    List<DonHangDTO> getDonHangCuaUser(String email);

    /** Lấy chi tiết một đơn hàng */
    DonHangDTO getDonHangById(String email, int maDonHang);

    /** Hủy đơn hàng (chỉ được hủy khi đang "Chờ xác nhận") */
    DonHangDTO huyDonHang(String email, int maDonHang, String lyDoHuy);

    /** Lấy danh sách đơn hàng mà người dùng là người bán (dựa trên sản phẩm trong đơn hàng) */
    List<DonHangDTO> getSellOrdersOfSeller(String email, String trangThai);

    /** Người bán xác nhận đơn hàng → chuyển trạng thái "Chờ duyệt" → "Đã duyệt" */
    DonHangDTO xacNhanDonHang(String sellerEmail, int maDonHang);

    /** Người bán hủy đơn hàng → chuyển trạng thái "Chờ duyệt" → "Đã hủy" */
    DonHangDTO huyDonHangBySeller(String sellerEmail, int maDonHang, String lyDoHuy);

    /** Admin: Lấy tất cả đơn hàng với filter trạng thái + phân trang */
    PageResponse<DonHangDTO> getAllOrdersForAdmin(String status, int page, int size);

    /** Admin: Xác nhận đơn hàng (không cần check seller) */
    DonHangDTO adminConfirmOrder(int maDonHang);

    /** Admin: Hủy đơn hàng */
    DonHangDTO adminCancelOrder(int maDonHang, String lyDoHuy);

    /** Admin: Cập nhật trạng thái đơn hàng sang trạng thái mới bất kỳ */
    DonHangDTO updateOrderStatus(int maDonHang, String trangThaiMoi);
}

