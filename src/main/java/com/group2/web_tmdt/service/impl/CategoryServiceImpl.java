package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.CategoryRepository;
import com.group2.web_tmdt.dto.CategoryDTO;
import com.group2.web_tmdt.dto.PageResponse;
import com.group2.web_tmdt.entity.Category;
import com.group2.web_tmdt.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(this::convertToDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByTenTheLoai(name);
        return category != null ? convertToDTO(category) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryDTO> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return buildPageResponse(categoryPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryDTO> searchCategories(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findByTenTheLoaiContainingIgnoreCase(keyword, pageable);
        return buildPageResponse(categoryPage);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getTenTheLoai() == null || categoryDTO.getTenTheLoai().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được trống");
        }
        if (categoryRepository.findByTenTheLoai(categoryDTO.getTenTheLoai().trim()) != null) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        Category category = new Category();
        category.setTenTheLoai(categoryDTO.getTenTheLoai().trim());
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(int id, CategoryDTO categoryDTO) {
        if (categoryDTO.getTenTheLoai() == null || categoryDTO.getTenTheLoai().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được trống");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        Category existing = categoryRepository.findByTenTheLoai(categoryDTO.getTenTheLoai().trim());
        if (existing != null && existing.getMaTheLoai() != id) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        category.setTenTheLoai(categoryDTO.getTenTheLoai().trim());
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("Danh mục đang chứa sản phẩm, không thể xóa!");
        }
        categoryRepository.deleteById(id);
    }

    private PageResponse<CategoryDTO> buildPageResponse(Page<Category> categoryPage) {
        List<CategoryDTO> content = categoryPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResponse.<CategoryDTO>builder()
                .content(content)
                .currentPage(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .first(categoryPage.isFirst())
                .last(categoryPage.isLast())
                .build();
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
            dto.setSoSanPham(activeProducts);
        } else {
            dto.setSoSanPham(0L);
        }
        
        return dto;
    }
}
