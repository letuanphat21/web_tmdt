package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.TinhTrangDTO;
import com.group2.web_tmdt.service.TinhTrangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TinhTrangController {

    private final TinhTrangService tinhTrangService;

    /**
     * GET /api/statuses
     * Lấy danh sách tất cả tình trạng sản phẩm
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TinhTrangDTO>>> getAllStatuses() {
        List<TinhTrangDTO> statuses = tinhTrangService.getAllStatuses();
        return ApiResponse.ok("Lấy danh sách tình trạng thành công!", statuses);
    }

    /**
     * GET /api/statuses/{id}
     * Lấy thông tin tình trạng theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TinhTrangDTO>> getStatusById(@PathVariable int id) {
        TinhTrangDTO status = tinhTrangService.getStatusById(id);
        return ApiResponse.ok("Lấy tình trạng thành công!", status);
    }

    /**
     * POST /api/statuses
     * Tạo tình trạng mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TinhTrangDTO>> createStatus(@RequestBody TinhTrangDTO tinhTrangDTO) {
        TinhTrangDTO createdStatus = tinhTrangService.createStatus(tinhTrangDTO);
        return ApiResponse.created("Tạo tình trạng thành công!", createdStatus);
    }

    /**
     * PUT /api/statuses/{id}
     * Cập nhật tình trạng
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TinhTrangDTO>> updateStatus(@PathVariable int id, 
                                                                   @RequestBody TinhTrangDTO tinhTrangDTO) {
        TinhTrangDTO updatedStatus = tinhTrangService.updateStatus(id, tinhTrangDTO);
        return ApiResponse.ok("Cập nhật tình trạng thành công!", updatedStatus);
    }

    /**
     * DELETE /api/statuses/{id}
     * Xóa tình trạng
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStatus(@PathVariable int id) {
        tinhTrangService.deleteStatus(id);
        return ApiResponse.ok("Xóa tình trạng thành công!", null);
    }
}
