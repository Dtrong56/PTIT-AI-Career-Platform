package com.ptit.ai_engine_service.dto.response;

import java.util.UUID;

public class CVPreprocessingResult {
    UUID cvId;
    String rawText;
    String structuredData;
    boolean success;
    String errorMessage;

    // constructor
    public CVPreprocessingResult(UUID cvId, String rawText, String structuredData, boolean success,
            String errorMessage) {
        this.cvId = cvId;
        this.rawText = rawText;
        this.structuredData = structuredData;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}