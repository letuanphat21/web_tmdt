package com.group2.web_tmdt.mapper;


import com.group2.web_tmdt.dto.ProductImageSeller;
import com.group2.web_tmdt.dto.ProductSellerDTO;
import com.group2.web_tmdt.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductSellerMapper {
    private final ProductImageSellerMapper productImageSellerMapper;

    public ProductSellerDTO toDTO(Product product){

        if(product == null){
            return null;
        }

        List<ProductImageSeller> images = product.getHinhAnhs() == null
                ? List.of()
                : product.getHinhAnhs().stream()
                        .map(productImageSellerMapper::toDTO)
                        .toList();

        return new ProductSellerDTO(
                product.getMaSanPham(),
                product.getTenSanPham(),
                product.getSoLuong(),
                product.getGiaSanPham(),
                product.getThuongHieu(),
                product.getMoTa(),
                product.getMauSac(),
                product.getKichCo(),
                product.getTrangThaiSanPham().getTenTrangThai(),
                product.getCategory().getMaTheLoai(),
                product.getCategory().getTenTheLoai(),
                product.getTinhTrang().getMaTinhTrang(),
                product.getTinhTrang().getTenTinhTrang(),
                product.isActive(),
                product.getSoLuongDaBan(),
                images
        );
    }
}
