package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.BinhLuanDTO;
import com.group2.web_tmdt.dto.TaoBinhLuanRequest;

import java.util.List;

public interface BinhLuanService {

    BinhLuanDTO taoBinhLuan(String email, TaoBinhLuanRequest request);

    List<BinhLuanDTO> getBinhLuanByProduct(long maSanPham);

    void xoaBinhLuan(String email, long maBinhLuan);
}
