package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.CategoryDTO;

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
}
