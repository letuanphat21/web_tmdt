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

    @Override
    public void quenMatKhau(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với email: " + email));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setMaKichHoat(otp);
        user.setThoiGianHetHanMaKichHoat(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.guiEmailQuenMatKhau(email, otp);
    }

    @Override
    public boolean xacNhanOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với email: " + email));

        if (user.getMaKichHoat() == null || !user.getMaKichHoat().equals(otp)) {
            return false;
        }

        if (user.getThoiGianHetHanMaKichHoat() == null
                || LocalDateTime.now().isAfter(user.getThoiGianHetHanMaKichHoat())) {
            return false;
        }

        return true;
    }

    @Override
    public void datLaiMatKhau(com.group2.web_tmdt.dto.ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp!");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với email: " + request.getEmail()));

        if (user.getMaKichHoat() == null || !user.getMaKichHoat().equals(request.getOtp())) {
            throw new BusinessException("Mã OTP không hợp lệ!");
        }

        if (user.getThoiGianHetHanMaKichHoat() == null
                || LocalDateTime.now().isAfter(user.getThoiGianHetHanMaKichHoat())) {
            throw new BusinessException("Mã OTP đã hết hạn!");
        }

        user.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        user.setMaKichHoat(null);
        user.setThoiGianHetHanMaKichHoat(null);
        userRepository.save(user);
    }

    @Override
    public void processOAuthPostLogin(String email, String name, String avatar, String googleId) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            // Default random password for OAuth2 users
            newUser.setMatKhau(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setTen(name);
            newUser.setAvatar(avatar);
            newUser.setGoogleId(googleId);
            newUser.setDaKichHoat(true); // Automatically activated since Google verified their email
            newUser.setActive(true);

            Optional<Role> roleUser = roleRepository.findByTenQuyen("ROLE_USER");
            roleUser.ifPresent(role -> newUser.setRoles(List.of(role)));

            userRepository.save(newUser);
        } else {
            User user = existUser.get();
            boolean needUpdate = false;

            // Update avatar if provided and user doesn't have one
            if (avatar != null && !avatar.isEmpty() && (user.getAvatar() == null || user.getAvatar().isEmpty())) {
                user.setAvatar(avatar);
                needUpdate = true;
            }
            // Update googleId if it's missing
            if (googleId != null && !googleId.isEmpty()
                    && (user.getGoogleId() == null || user.getGoogleId().isEmpty())) {
                user.setGoogleId(googleId);
                needUpdate = true;
            }
            if(name !=null && !name.isEmpty() && (user.getTen() == null || user.getTen().isEmpty())) {
                user.setTen(name);
                needUpdate = true;
            }

            if (needUpdate) {
                userRepository.save(user);
            }
        }
    }
}
