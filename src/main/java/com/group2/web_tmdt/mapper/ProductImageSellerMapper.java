package com.group2.web_tmdt.mapper;


import com.group2.web_tmdt.dto.ProductImageResponse;
import com.group2.web_tmdt.dto.ProductImageSeller;
import com.group2.web_tmdt.entity.HinhAnh;
import org.springframework.stereotype.Component;

@Component
public class ProductImageSellerMapper {

    public ProductImageSeller toDTO(HinhAnh hinhAnh) {
        if(hinhAnh == null){
            return null;
        }

        return new ProductImageSeller(
                hinhAnh.getMaHinhAnh(),
                hinhAnh.getTenHinhAnh(),
                hinhAnh.getDuongDan()
        );
    }

}
