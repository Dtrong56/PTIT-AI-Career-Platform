package com.ptit.core_platform_service.messaging;

import java.util.UUID;

public record CVPreprocessingResult(
        UUID cvId,
        String structuredContent,
        boolean success,
        String errorMessage
) {
}