package com.group2.web_tmdt.mapper;


import com.group2.web_tmdt.dto.ProductImageResponse;
import com.group2.web_tmdt.dto.TrangThaiSanPhamDTO;
import com.group2.web_tmdt.entity.TrangThaiSanPham;
import org.springframework.stereotype.Component;

@Component
public class TrangThaiSanPhamMapper {

    public TrangThaiSanPhamDTO toDT0(TrangThaiSanPham trangThaiSanPham){
        if(trangThaiSanPham == null){
            return null;
        }

        return new TrangThaiSanPhamDTO(
                trangThaiSanPham.getId(),
                trangThaiSanPham.getTenTrangThai()
        );
    }

}
