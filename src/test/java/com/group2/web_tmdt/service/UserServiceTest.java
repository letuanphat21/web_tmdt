package com.group2.web_tmdt.service;


import com.group2.web_tmdt.dao.RoleRepository;
import com.group2.web_tmdt.dao.UserRepository;
import com.group2.web_tmdt.dto.RegisterRequest;
import com.group2.web_tmdt.entity.Role;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.exception.BusinessException;
import com.group2.web_tmdt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp(){
        registerRequest = new RegisterRequest("ltphat240103@gmail.com","phat1234","phat1234");
    }

    @Test
    void register_valid_success(){
        //Given
        Role role = new Role();
        role.setTenQuyen("ROLE_USER");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

        when(roleRepository.findByTenQuyen("ROLE_USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoderPassword");

        // WHEN
        userServiceImpl.register(registerRequest);

        // THEN

        ArgumentCaptor<User> userCaptor= ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        Assertions.assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        Assertions.assertFalse(savedUser.isDaKichHoat());
        Assertions.assertTrue(savedUser.isActive());

        verify(emailService)
                .guiEmailKichHoat(eq(registerRequest.getEmail()), anyString());

        verify(userRepository,times(1)).existsByEmail(registerRequest.getEmail());
    }

    @Test
    void register_passwordNotMatch_throwException(){
        // Given
        registerRequest.setConfirmPassword("123456");

        // WHen
        BusinessException exception = assertThrows(BusinessException.class,() -> userServiceImpl.register(registerRequest));

        // Then
        Assertions.assertEquals("Mật khẩu xác nhận không khớp!", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(emailService, never()).guiEmailKichHoat(anyString(), anyString());
    }

    @Test
    void register_emailHasExist(){
        // GIVEN
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        //WHEN
        BusinessException exception = assertThrows(BusinessException.class,() -> userServiceImpl.register(registerRequest));

        // Then
        Assertions.assertEquals("Email đã được sử dụng!", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(emailService, never()).guiEmailKichHoat(anyString(), anyString());
    }


    @Test
    void active_account_valid_success() {
        // GIVEN
        User user = new User();
        user.setThoiGianHetHanMaKichHoat(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByMaKichHoat(anyString()))
                .thenReturn(Optional.of(user));

        // WHEN
        boolean result = userServiceImpl.kichHoatTaiKhoan("ABC123");

        // THEN
        Assertions.assertTrue(result);
        Assertions.assertTrue(user.isDaKichHoat());
        Assertions.assertNull(user.getMaKichHoat());
        Assertions.assertNull(user.getThoiGianHetHanMaKichHoat());

        verify(userRepository).save(user);
    }

    @Test
    void active_account_NotFoundMaKichHoat(){
        when(userRepository.findByMaKichHoat(anyString())).thenReturn(Optional.empty());

        boolean result = userServiceImpl.kichHoatTaiKhoan("maKichHoat");

        Assertions.assertFalse(result);
    }

    @Test
    void active_account_MaKichHoatHetThoiGian(){
        User user = new User();
        user.setThoiGianHetHanMaKichHoat(LocalDateTime.now().minusMinutes(5));

        when(userRepository.findByMaKichHoat(anyString()))
                .thenReturn(Optional.of(user));

        boolean result = userServiceImpl.kichHoatTaiKhoan("maKichHoat");

        Assertions.assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void active_account_MaKichHoatNull(){
        User user = new User();
        user.setThoiGianHetHanMaKichHoat(null);

        when(userRepository.findByMaKichHoat(anyString()))
                .thenReturn(Optional.of(user));

        boolean result = userServiceImpl.kichHoatTaiKhoan("maKichHoat");

        Assertions.assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserByemail_valid_success(){
        // GIVEN
        User user = new User();
        String email = "abc@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // WHEN
        Optional<User> result = userServiceImpl.findByEmail(email);

        // THEN
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user, result.get());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUserName_valid_success(){
        // GIVEN
        User user = new User();
        Role role = new Role();
        role.setTenQuyen("ROLE_USER");

        user.setEmail("ltphat240103@gmail.com");
        user.setMatKhau("123456");
        user.setActive(true);
        user.setDaKichHoat(true);
        user.setRoles(List.of(role));

        when(userRepository.findByEmail("ltphat240103@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = userServiceImpl.loadUserByUsername("ltphat240103@gmail.com");

        // THEN
        Assertions.assertEquals(user.getEmail(), result.getUsername());
        Assertions.assertEquals(user.getMatKhau(), result.getPassword());
        Assertions.assertTrue(
                result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void loadUserByUserName_valid_fail_notFindUserByEmail(){
        String email="ltphat240103@gmail.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,()-> userServiceImpl.loadUserByUsername(email));

        Assertions.assertEquals("Không tìm thấy người dùng với email: " + email,exception.getMessage());
        verify(userRepository,times(1)).findByEmail(email);

    }

    @Test
    void loadUserByUserName_valid_fail_notKichHoat(){
        User user = new User();
        user.setEmail("ltphat240103@gmail.com");
        user.setDaKichHoat(false);

        when(userRepository.findByEmail("ltphat240103@gmail.com"))
                .thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,()->userServiceImpl.loadUserByUsername("ltphat240103@gmail.com"));

        Assertions.assertEquals("Tài khoản chưa được kích hoạt",exception.getMessage());

    }

    @Test
    void loadUserByUserName_valid_fail_notActive(){
        User user = new User();
        user.setEmail("ltphat240103@gmail.com");
        user.setDaKichHoat(true);
        user.setActive(false);

        when(userRepository.findByEmail("ltphat240103@gmail.com"))
                .thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,()->userServiceImpl.loadUserByUsername("ltphat240103@gmail.com"));

        Assertions.assertEquals("Tài khoản đã bị khóa",exception.getMessage());

    }

    @Test
    void quenMatKhau_valid_success(){

    }




}
