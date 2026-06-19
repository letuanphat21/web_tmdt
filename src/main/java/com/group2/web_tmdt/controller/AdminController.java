package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dto.AdminUserDTO;
import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.CategoryDTO;
import com.group2.web_tmdt.dto.DonHangDTO;
import com.group2.web_tmdt.dto.PageResponse;
import com.group2.web_tmdt.service.AdminService;
import com.group2.web_tmdt.service.CategoryService;
import com.group2.web_tmdt.service.DonHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DonHangService donHangService;
    private final CategoryService categoryService;

    // ─── User management ──────────────────────────────────────────────────────

    /** GET /api/admin/users?page=0&size=10 */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.getAllUsers(page, size);
        return ApiResponse.ok("Lấy danh sách người dùng thành công!", response);
    }

    /** GET /api/admin/users/search?q=keyword&page=0&size=10 */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> searchUsers(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.searchUsers(keyword, page, size);
        return ApiResponse.ok("Tìm kiếm người dùng thành công!", response);
    }

    /** PUT /api/admin/users/{id}/status */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        AdminUserDTO response = adminService.updateUserStatus(id, request.getTrangThai());
        return ApiResponse.ok("Cập nhật trạng thái thành công!", response);
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ApiResponse.ok("Xóa người dùng thành công!", null);
    }

    /** POST /api/admin/users */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<AdminUserDTO>> createUser(@RequestBody AdminUserDTO userDTO) {
        AdminUserDTO response = adminService.createUser(userDTO);
        return ApiResponse.ok("Tạo người dùng thành công!", response);
    }

    /** GET /api/admin/users/hidden?page=0&size=10 */
    @GetMapping("/users/hidden")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> getHiddenUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.getAllHiddenUsers(page, size);
        return ApiResponse.ok("Lấy danh sách người dùng bị ẩn thành công!", response);
    }

    /** GET /api/admin/users/hidden/search?q=keyword&page=0&size=10 */
    @GetMapping("/users/hidden/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserDTO>>> searchHiddenUsers(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserDTO> response = adminService.searchHiddenUsers(keyword, page, size);
        return ApiResponse.ok("Tìm kiếm người dùng bị ẩn thành công!", response);
    }

    // ─── Order management ─────────────────────────────────────────────────────

    /**
     * GET /api/admin/orders?status=all&page=0&size=10
     * Lấy tất cả đơn hàng với filter trạng thái và phân trang
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<DonHangDTO>>> getAllOrders(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<DonHangDTO> response = donHangService.getAllOrdersForAdmin(status, page, size);
        return ApiResponse.ok("Lấy danh sách đơn hàng thành công!", response);
    }

    /**
     * PUT /api/admin/orders/{id}/confirm
     * Admin xác nhận đơn hàng → "Đã duyệt"
     */
    @PutMapping("/orders/{id}/confirm")
    public ResponseEntity<ApiResponse<DonHangDTO>> adminConfirmOrder(@PathVariable int id) {
        DonHangDTO donHang = donHangService.adminConfirmOrder(id);
        return ApiResponse.ok("Xác nhận đơn hàng thành công!", donHang);
    }

    /**
     * PUT /api/admin/orders/{id}/cancel
     * Admin hủy đơn hàng → "Đã hủy"
     */
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<DonHangDTO>> adminCancelOrder(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {
        String lyDoHuy = (String) body.getOrDefault("lyDoHuy", "Admin hủy đơn");
        DonHangDTO donHang = donHangService.adminCancelOrder(id, lyDoHuy);
        return ApiResponse.ok("Hủy đơn hàng thành công!", donHang);
    }

    /**
     * PUT /api/admin/orders/{id}/status
     * Admin chuyển đổi trạng thái đơn hàng sang trạng thái khác
     */
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<DonHangDTO>> updateOrderStatus(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {
        String trangThaiMoi = (String) body.get("trangThai");
        if (trangThaiMoi == null || trangThaiMoi.isBlank()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Vui lòng nhập trạng thái mới");
        }
        DonHangDTO donHang = donHangService.updateOrderStatus(id, trangThaiMoi);
        return ApiResponse.ok("Cập nhật trạng thái đơn hàng thành công!", donHang);
    }

    // ─── Category management ──────────────────────────────────────────────────

    /** GET /api/admin/categories?page=0&size=10 */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<PageResponse<CategoryDTO>>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CategoryDTO> response = categoryService.getAllCategories(page, size);
        return ApiResponse.ok("Lấy danh sách danh mục thành công!", response);
    }

    /** GET /api/admin/categories/search?q=keyword&page=0&size=10 */
    @GetMapping("/categories/search")
    public ResponseEntity<ApiResponse<PageResponse<CategoryDTO>>> searchCategories(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CategoryDTO> response = categoryService.searchCategories(keyword, page, size);
        return ApiResponse.ok("Tìm kiếm danh mục thành công!", response);
    }

    /** POST /api/admin/categories */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.createCategory(categoryDTO);
        return ApiResponse.ok("Tạo danh mục thành công!", response);
    }

    /** PUT /api/admin/categories/{id} */
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable int id,
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.updateCategory(id, categoryDTO);
        return ApiResponse.ok("Cập nhật danh mục thành công!", response);
    }

    /** DELETE /api/admin/categories/{id} */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ApiResponse.ok("Xóa danh mục thành công!", null);
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StatusUpdateRequest {
        private Integer trangThai;
    }
}
