package com.ptit.dashboard_service.messaging.dto;

import java.util.UUID;

public record CVAddRequest(
        UUID cvId,
        UUID studentId) {
}