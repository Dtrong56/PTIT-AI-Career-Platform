package com.ptit.identity_service.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String role,
        boolean mustChangePassword
) {
}