package com.ptit.ai_engine_service.dto.request;

import java.util.UUID;

public record CVPreprocessingRequest(
        UUID cvId,
        UUID studentId,
        String fileName,
        String contentType,
        byte[] fileData) {
}