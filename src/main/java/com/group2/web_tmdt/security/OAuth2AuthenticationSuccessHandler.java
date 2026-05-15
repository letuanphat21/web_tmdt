package com.group2.web_tmdt.security;

import com.group2.web_tmdt.service.JWTService;
import com.group2.web_tmdt.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatar = oAuth2User.getAttribute("picture"); // Google dùng "picture"
        String googleId = oAuth2User.getAttribute("sub"); // Google dùng "sub" cho user ID

        // Tạo user mới hoặc cập nhật user cũ (link googleId)
        userService.processOAuthPostLogin(email, name, avatar, googleId);

        // Generate JWT của hệ thống (không phải token của Google)
        String accessToken = jwtService.generateToken(email);
        String refreshToken = jwtService.createRefreshToken(email);

        // Lưu refresh token vào HttpOnly cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        // cookie.setSecure(true); // Bỏ comment nếu chạy HTTPS
        response.addCookie(cookie);

        // Redirect về frontend kèm access token trong URL
        String targetUrl = Endpoints.front_end_host + "/?token=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
