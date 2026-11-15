package com.ptit.dashboard_service.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class CurrentUser {

    private CurrentUser() {
    }

    private static Jwt getJwt() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException(
                    "User is not authenticated");
        }

        return jwt;
    }

    public static String getUsername() {
        return getJwt().getSubject();
    }

    public static String getRole() {
        return getJwt().getClaimAsString("role");
    }

    public static boolean isMustChangePassword() {
        Boolean value = getJwt().getClaim("mustChangePassword");
        return Boolean.TRUE.equals(value);
    }
}