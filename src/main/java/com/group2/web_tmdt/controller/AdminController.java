package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.AdminUserDTO;
import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.PageResponse;
import com.group2.web_tmdt.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * GET /api/admin/users?page=0&size=10
     * Lấy danh sách người dùng active với phân trang
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.getAllUsers(page, size);
        return ApiResponse.ok("Lấy danh sách người dùng thành công!", response);
    }

    /**
     * GET /api/admin/users/search?q=keyword&page=0&size=10
     * Tìm kiếm người dùng active theo keyword (tên, email)
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> searchUsers(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.searchUsers(keyword, page, size);
        return ApiResponse.ok("Tìm kiếm người dùng thành công!", response);
    }

    /**
     * PUT /api/admin/users/{id}/status
     * Cập nhật trạng thái active của người dùng
     * Body: { "trangThai": 1 } (1 = active, 0 = inactive)
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        AdminUserDTO response = adminService.updateUserStatus(id, request.getTrangThai());
        return ApiResponse.ok("Cập nhật trạng thái thành công!", response);
    }

    /**
     * DELETE /api/admin/users/{id}
     * Xóa người dùng
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ApiResponse.ok("Xóa người dùng thành công!", null);
    }

    /**
     * GET /api/admin/users/hidden?page=0&size=10
     * Lấy danh sách người dùng bị ẩn (active = false) với phân trang
     */
    @GetMapping("/users/hidden")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> getHiddenUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.getAllHiddenUsers(page, size);
        return ApiResponse.ok("Lấy danh sách người dùng bị ẩn thành công!", response);
    }

    /**
     * GET /api/admin/users/hidden/search?q=keyword&page=0&size=10
     * Tìm kiếm người dùng bị ẩn (active = false) theo keyword
     */
    @GetMapping("/users/hidden/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> searchHiddenUsers(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.searchHiddenUsers(keyword, page, size);
        return ApiResponse.ok("Tìm kiếm người dùng bị ẩn thành công!", response);
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StatusUpdateRequest {
        private Integer trangThai;
    }
}
