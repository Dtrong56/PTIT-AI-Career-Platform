
package com.ptit.identity_service.security;

import com.ptit.identity_service.entity.Account;
import com.ptit.identity_service.repository.AccountRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AccountRepository accountRepository;

    public SecurityConfig(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Đăng nhập và kiểm tra service
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/test")
                        .permitAll()

                        // Đổi mật khẩu lần đầu:
                        // Phải có JWT và tài khoản đang yêu cầu đổi mật khẩu
                        .requestMatchers("/api/auth/change-password")
                        .access((authentication, context) ->
                                new AuthorizationDecision(
                                        isAccountAllowed(
                                                authentication.get(),
                                                null,
                                                true)))

                        // API Khoa: chỉ FACULTY đã đổi mật khẩu
                        // và tài khoản vẫn đang hoạt động
                        .requestMatchers("/api/faculty/**")
                        .access((authentication, context) ->
                                new AuthorizationDecision(
                                        isAccountAllowed(
                                                authentication.get(),
                                                "FACULTY",
                                                false)))

                        // Các API còn lại:
                        // Phải đăng nhập, tài khoản hoạt động
                        // và đã hoàn thành đổi mật khẩu
                        .anyRequest()
                        .access((authentication, context) ->
                                new AuthorizationDecision(
                                        isAccountAllowed(
                                                authentication.get(),
                                                null,
                                                false)))
                )

                // Xác thực JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter())));

        return http.build();
    }

    // Kiểm tra quyền truy cập dựa trên trạng thái tài khoản trong database
    private boolean isAccountAllowed(
            Authentication authentication,
            String requiredRole,
            boolean expectedMustChangePassword) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return false;
        }

        String username = jwtAuth.getToken().getSubject();

        if (username == null || username.isBlank()) {
            return false;
        }

        // Đọc trạng thái mật khẩu trong JWT
        Object tokenFlag = jwtAuth.getToken()
                .getClaims()
                .get("mustChangePassword");

        if (!(tokenFlag instanceof Boolean tokenMustChangePassword)) {
            return false;
        }

        // Đọc tài khoản mới nhất từ database
        return accountRepository.findByUsername(username)
                .map(account -> {

                    // Tài khoản bị khóa
                    if (!account.isEnabled()) {
                        return false;
                    }

                    // JWT phải khớp trạng thái hiện tại trong database
                    if (tokenMustChangePassword
                            != account.isMustChangePassword()) {
                        return false;
                    }

                    // Kiểm tra tài khoản đã hoàn thành đổi mật khẩu chưa
                    if (account.isMustChangePassword()
                            != expectedMustChangePassword) {
                        return false;
                    }

                    // Kiểm tra role trong database
                    if (requiredRole != null
                            && !requiredRole.equals(account.getRole())) {
                        return false;
                    }

                    // Kiểm tra quyền trong JWT có khớp database không
                    return jwtAuth.getAuthorities().stream()
                            .anyMatch(authority ->
                                    account.getRole().equals(
                                            authority.getAuthority()));
                })
                .orElse(false);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter);

        return converter;
    }
}
