package com.ptit.identity_service.dto;

public record StudentSummaryResponse(
        String studentId,
        String fullName,
        String email
) {
}