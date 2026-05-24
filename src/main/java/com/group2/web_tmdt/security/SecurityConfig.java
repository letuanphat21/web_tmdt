package com.group2.web_tmdt.security;

import com.group2.web_tmdt.filter.JwtAuthFilter;
import com.group2.web_tmdt.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService,
            PasswordEncoder passwordEncoder,
            OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2FailureHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.oAuth2FailureHandler = oAuth2FailureHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_PUT_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,Endpoints.ADMIN_GET_ENDPOINTS).hasRole("ADMIN")
                .anyRequest().authenticated());

        // IF_REQUIRED: OAuth2 cần session tạm thời trong lúc redirect Google
        // Sau khi callback xong thì JWT stateless tiếp quản, session không còn dùng nữa
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Cấu hình OAuth2 Login - chỉ dùng Google để xác thực, sau đó dùng JWT
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(auth ->
                        auth.baseUri("/api/oauth2/authorization")
                )
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler));

        http.exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        PathPatternRequestMatcher.withDefaults().matcher("/api/**")));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(Endpoints.front_end_host,Endpoints.front_end_host2));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
