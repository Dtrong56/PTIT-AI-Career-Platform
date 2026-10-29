package com.ptit.identity_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Cấu hình bảo mật cho Identity Service.
     *
     * Hiện tại đang ở giai đoạn phát triển/test nên:
     * - Tắt CSRF để thuận tiện test REST API.
     * - Tắt form login mặc định của Spring Security.
     * - Cho phép endpoint /api/auth/test truy cập không cần đăng nhập.
     *
     * Sau này khi triển khai JWT:
     * - Các endpoint login/register vẫn permitAll().
     * - Các API cần bảo vệ sẽ yêu cầu JWT.
     * - Có thể bổ sung JWT filter/resource server configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST API không sử dụng form CSRF mặc định
                .csrf(csrf -> csrf.disable())

                // Không sử dụng trang đăng nhập mặc định của Spring Security
                .formLogin(form -> form.disable())

                // Cấu hình quyền truy cập endpoint
                .authorizeHttpRequests(auth -> auth
                        // Endpoint dùng để kiểm tra service
                        .requestMatchers("/api/auth/test").permitAll()

                        // Các endpoint khác tạm thời cho phép để thuận tiện phát triển
                        // Sau này sẽ thay bằng cấu hình JWT
                        .anyRequest().permitAll());

        return http.build();
    }
}
