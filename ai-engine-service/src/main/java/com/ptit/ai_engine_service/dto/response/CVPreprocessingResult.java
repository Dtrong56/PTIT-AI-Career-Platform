package com.ptit.ai_engine_service.dto.response;

import java.util.UUID;

public record CVPreprocessingResult(
        UUID cvId,
        String rawText,
        String structuredData,
        boolean success,
        String errorMessage) {
}