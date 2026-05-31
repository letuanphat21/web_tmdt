package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.AdminUserDTO;
import com.group2.web_tmdt.dto.PageResponse;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllActive(pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserDTO> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchActive(keyword, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    public AdminUserDTO updateUserStatus(Long maNguoiDung, Integer trangThai) {
        User user = userRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // trangThai: 1 = active, 0 = inactive
        boolean newActive = trangThai == 1;
        user.setActive(newActive);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long maNguoiDung) {
        if (!userRepository.existsById(maNguoiDung)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        userRepository.deleteById(maNguoiDung);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserDTO> getAllHiddenUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllHidden(pageable);
        return buildPageResponse(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserDTO> searchHiddenUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchHidden(keyword, pageable);
        return buildPageResponse(userPage);
    }

    @Override
    public AdminUserDTO createUser(AdminUserDTO userDTO) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setHoDem(userDTO.getHoDem());
        user.setTen(userDTO.getTen());
        user.setDiaChi(userDTO.getDiaChi());
        user.setGioiTinh(userDTO.getGioiTinh());
        user.setBirthDay(userDTO.getNgaySinh());
        user.setActive(true); // Mặc định active = true
        // Password sẽ được mã hóa nếu có (set ở controller)
        user.setMatKhau("a1234567"); // Default password

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private PageResponse<AdminUserDTO> buildPageResponse(Page<User> userPage) {
        List<AdminUserDTO> content = userPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResponse.<AdminUserDTO>builder()
                .content(content)
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    private AdminUserDTO convertToDTO(User user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setMaNguoiDung(user.getMaNguoiDung());
        dto.setEmail(user.getEmail());
        dto.setHoDem(user.getHoDem());
        dto.setTen(user.getTen());
        dto.setAvatar(user.getAvatar());
        dto.setDiaChi(user.getDiaChi());
        dto.setGioiTinh(user.getGioiTinh());
        dto.setNgaySinh(user.getBirthDay());
        // trangThai: 1 = active, 0 = inactive
        dto.setTrangThai(user.isActive() ? 1 : 0);
        return dto;
    }
}
