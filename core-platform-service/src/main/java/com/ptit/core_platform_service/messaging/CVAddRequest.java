package com.ptit.core_platform_service.messaging;

import java.util.UUID;

public record CVAddRequest(
        UUID cvId,
        UUID studentId) {
}