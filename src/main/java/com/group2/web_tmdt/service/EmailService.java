package com.group2.web_tmdt.service;

public interface EmailService {

    void guiEmailKichHoat(String toEmail, String maKichHoat);

    void guiEmailQuenMatKhau(String toEmail, String otp);

    void guiEmailTuChoi(String toEmail, String lyDo);

    void guiEmailDonHangMoiChoSeller(String sellerEmail, String buyerName,
                                      int maDonHang, String diaChiNhanHang,
                                      double tongTien, String chiTietSanPham);
}
