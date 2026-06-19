package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.CategoryDTO;
import com.group2.web_tmdt.dto.PageResponse;

import java.util.List;

public interface CategoryService {

    /**
     * Lấy tất cả danh mục
     */
    List<CategoryDTO> getAllCategories();

    /**
     * Lấy danh mục theo ID
     */
    CategoryDTO getCategoryById(int id);

    /**
     * Lấy danh mục theo tên
     */
    CategoryDTO getCategoryByName(String name);

    /**
     * Lấy danh sách danh mục phân trang
     */
    PageResponse<CategoryDTO> getAllCategories(int page, int size);

    /**
     * Tìm kiếm danh mục phân trang
     */
    PageResponse<CategoryDTO> searchCategories(String keyword, int page, int size);

    /**
     * Tạo danh mục mới
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Cập nhật danh mục
     */
    CategoryDTO updateCategory(int id, CategoryDTO categoryDTO);

    /**
     * Xóa danh mục
     */
    void deleteCategory(int id);
}
