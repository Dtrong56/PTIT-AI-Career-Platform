package com.ptit.identity_service.dto;

public record LoginRequest(
        String studentId,
        String password
) {
}