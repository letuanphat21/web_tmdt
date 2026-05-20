package com.group2.web_tmdt.mapper;

import com.group2.web_tmdt.dto.ProductImageResponse;
import com.group2.web_tmdt.entity.HinhAnh;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper {

    public ProductImageResponse toDTO(HinhAnh hinhAnh){

        if(hinhAnh == null){
            return null;
        }

        return new ProductImageResponse(
                hinhAnh.getTenHinhAnh(),
                hinhAnh.getDuongDan()
        );
    }
}
