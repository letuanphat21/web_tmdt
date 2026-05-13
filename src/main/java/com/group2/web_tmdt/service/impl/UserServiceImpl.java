package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.RoleRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.RegisterRequest;
import com.group2.web_tmdt.entity.Role;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.exception.BusinessException;
import com.group2.web_tmdt.service.UserService;
import lombok.RequiredArgsConstructor;
import com.group2.web_tmdt.service.EmailService;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public void register(RegisterRequest request) {
        // Kiểm tra 2 mật khẩu có khớp không
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp!");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã được sử dụng!");
        }

        // Tạo mã kích hoạt ngẫu nhiên + đặt thời hạn 24h
        String maKichHoat = UUID.randomUUID().toString();
        LocalDateTime thoiGianHetHan = LocalDateTime.now().plusHours(24);

        // Tạo User mới (thông tin chi tiết sẽ cập nhật sau ở phần chỉnh sửa hồ sơ)
        User user = new User();
        user.setEmail(request.getEmail());
        user.setMatKhau(passwordEncoder.encode(request.getPassword()));
        user.setDaKichHoat(false); // Chưa kích hoạt
        user.setActive(true);
        user.setMaKichHoat(maKichHoat);
        user.setThoiGianHetHanMaKichHoat(thoiGianHetHan);

        // Gán quyền USER mặc định (maQuyen = 2, hoặc tìm theo tên)
        Optional<Role> roleUser = roleRepository.findByTenQuyen("ROLE_USER");
        roleUser.ifPresent(role -> user.setRoles(List.of(role)));

        userRepository.save(user);

        // Gửi email kích hoạt
        emailService.guiEmailKichHoat(user.getEmail(), maKichHoat);
    }

    @Override
    public boolean kichHoatTaiKhoan(String maKichHoat) {
        Optional<User> optUser = userRepository.findByMaKichHoat(maKichHoat);
        if (optUser.isEmpty()) {
            return false;
        }
        User user = optUser.get();

        // Kiểm tra mã kích hoạt có còn trong thời hạn không
        if (user.getThoiGianHetHanMaKichHoat() == null
                || LocalDateTime.now().isAfter(user.getThoiGianHetHanMaKichHoat())) {
            return false; // Mã đã hết hạn
        }

        user.setDaKichHoat(true);
        user.setMaKichHoat(null); // Xóa mã sau khi kích hoạt
        user.setThoiGianHetHanMaKichHoat(null); // Xóa thời hạn
        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        if (!user.isDaKichHoat()) {
            throw new BusinessException("Tài khoản chưa được kích hoạt");
        }

        if (!user.isActive()) {
            throw new BusinessException("Tài khoản đã bị khóa");
        }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getTenQuyen()))
                .toList();

        // Này là UserDetail của security nha
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getMatKhau(), authorities);
    }
}
