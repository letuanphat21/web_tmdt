package com.group2.web_tmdt.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = exception.getMessage() != null
                ? exception.getMessage()
                : "Đăng nhập Google thất bại. Vui lòng thử lại.";

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // Redirect về trang login kèm thông báo lỗi trong query param
        String targetUrl = Endpoints.front_end_host + "/login?error=" + encodedMessage;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
