package com.group2.web_tmdt.mapper;

import com.group2.web_tmdt.dto.ProductImageResponse;
import com.group2.web_tmdt.dto.ProductPendingDTO;
import com.group2.web_tmdt.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductImageMapper productImageMapper;

    public ProductPendingDTO toPendingDTO(Product product){

        if(product == null){
            return null;
        }

        List<ProductImageResponse> images =
                product.getHinhAnhs()
                        .stream()
                        .map(productImageMapper::toDTO)
                        .toList();

        return new ProductPendingDTO(
                product.getMaSanPham(),
                product.getTenSanPham(),
                product.getSoLuong(),
                product.getGiaSanPham(),
                product.getThuongHieu(),
                product.getMoTa(),
                product.getMauSac(),
                product.getKichCo(),
                product.getUser().getEmail(),
                product.getCategory().getTenTheLoai(),
                product.getTinhTrang().getTenTinhTrang(),
                product.getTrangThaiSanPham().getTenTrangThai(),
                product.isActive(),
                product.getSoLuongDaBan(),
                images
        );
    }
}
