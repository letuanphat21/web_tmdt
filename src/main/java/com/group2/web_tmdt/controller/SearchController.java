package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.ProductDTO;
import com.group2.web_tmdt.dto.TinhTrangDTO;
import com.group2.web_tmdt.service.ProductService;
import com.group2.web_tmdt.service.TinhTrangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {

    private final ProductService productService;
    private final TinhTrangService tinhTrangService;

    /**
     * GET /api/search
     * Tìm kiếm sản phẩm với bộ lọc kết hợp
     *
     * Query params:
     * - keyword: từ khóa tìm kiếm (mặc định: null)
     * - categoryId: ID danh mục (mặc định: null)
     * - statusId: ID tình trạng sản phẩm (mặc định: null)
     * - minPrice: giá tối thiểu (mặc định: null)
     * - maxPrice: giá tối đa (mặc định: null)
     * - page: trang (mặc định: 0)
     * - size: số lượng sản phẩm trên mỗi trang (mặc định: 12)
     * - sort: trường sắp xếp (mặc định: maSanPham)
     * - direction: hướng sắp xếp ASC/DESC (mặc định: DESC)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "maSanPham") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.valueOf(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ProductDTO> result = productService.searchProducts(keyword, categoryId, statusId, minPrice, maxPrice, pageable);

        return ApiResponse.ok("Tìm kiếm sản phẩm thành công!", result);
    }

    /**
     * GET /api/search/statuses
     * Lấy danh sách tất cả tình trạng sản phẩm
     */
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<List<TinhTrangDTO>>> getAllStatuses() {
        List<TinhTrangDTO> statuses = tinhTrangService.getAllStatuses();
        return ApiResponse.ok("Lấy danh sách tình trạng thành công!", statuses);
    }

    /**
     * GET /api/search/filter-options
     * Lấy các tùy chọn lọc (danh mục, tình trạng)
     */
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilterOptions() {
        Map<String, Object> filterOptions = new HashMap<>();

        filterOptions.put("statuses", tinhTrangService.getAllStatuses());

        return ApiResponse.ok("Lấy tùy chọn lọc thành công!", filterOptions);
    }
}
