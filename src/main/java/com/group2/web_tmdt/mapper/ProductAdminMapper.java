package com.group2.web_tmdt.mapper;


import com.group2.web_tmdt.dto.ProductAdminDTO;
import com.group2.web_tmdt.dto.ProductImageResponse;
import com.group2.web_tmdt.dto.ProductImageSeller;
import com.group2.web_tmdt.dto.ProductSellerDTO;
import com.group2.web_tmdt.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductAdminMapper {
    private final ProductImageMapper ProductImageMapper;

    public ProductAdminDTO toDTO(Product product){

        if(product == null){
            return null;
        }

        ProductImageResponse images =
                product.getHinhAnhs() == null
                        ? new ProductImageResponse()
                        : ProductImageMapper.toDTO(product.getHinhAnhs().get(0));

        return new ProductAdminDTO(
                product.getMaSanPham(),
                product.getTenSanPham(),
                product.getSoLuong(),
                product.getGiaSanPham(),
                product.getUser().getEmail(),
                product.getTrangThaiSanPham().getTenTrangThai(),
                product.isActive(),
                images
        );
    }
}
