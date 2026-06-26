package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dto.AdminUserDTO;
import com.group2.web_tmdt.dto.PageResponse;

public interface AdminService {
    /**
     * Lấy danh sách người dùng active với phân trang
     */
    PageResponse<AdminUserDTO> getAllUsers(int page, int size);

    /**
     * Tìm kiếm người dùng active theo keyword
     */
    PageResponse<AdminUserDTO> searchUsers(String keyword, int page, int size);

    /**
     * Cập nhật trạng thái active của người dùng
     */
    AdminUserDTO updateUserStatus(Long maNguoiDung, Integer trangThai);

    /**
     * Xóa người dùng
     */
    void deleteUser(Long maNguoiDung);

    /**
     * Lấy danh sách người dùng bị ẩn (active = false) với phân trang
     */
    PageResponse<AdminUserDTO> getAllHiddenUsers(int page, int size);

    /**
     * Tìm kiếm người dùng bị ẩn (active = false) theo keyword
     */
    PageResponse<AdminUserDTO> searchHiddenUsers(String keyword, int page, int size);

    /**
     * Tạo người dùng mới
     */
    AdminUserDTO createUser(AdminUserDTO userDTO);

    /**
     * Cập nhật thông tin người dùng
     */
    AdminUserDTO updateUser(Long maNguoiDung, AdminUserDTO userDTO);
}
