package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.SellerDTO;

import java.util.List;

public interface SellerService {

    /**
     * Lấy thông tin người bán theo ID
     */
    SellerDTO getSellerById(long id);

    /**
     * Lấy danh sách top người bán có xếp hạng cao nhất
     */
    List<SellerDTO> getTopRatedSellers(int limit);

    /**
     * Lấy danh sách top người bán có sản phẩm nhiều nhất
     */
    List<SellerDTO> getTopSellersByProductCount(int limit);
}
