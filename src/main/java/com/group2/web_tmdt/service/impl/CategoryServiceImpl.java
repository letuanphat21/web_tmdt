package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.CategoryRepository;
import com.group2.web_tmdt.dto.CategoryDTO;
import com.group2.web_tmdt.entity.Category;
import com.group2.web_tmdt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(this::convertToDTO).orElse(null);
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByTenTheLoai(name);
        return category != null ? convertToDTO(category) : null;
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setMaTheLoai(category.getMaTheLoai());
        dto.setTenTheLoai(category.getTenTheLoai());
        
        // Tính số sản phẩm active=1 và ma_trang_thai=2 trong danh mục
        if (category.getProducts() != null) {
            long activeProducts = category.getProducts().stream()
                    .filter(p -> p.isActive() && p.getTrangThaiSanPham() != null && p.getTrangThaiSanPham().getId() == 2)
                    .count();
            dto.setSoSanPham((int) activeProducts);
        } else {
            dto.setSoSanPham(0);
        }
        
        return dto;
    }
}
