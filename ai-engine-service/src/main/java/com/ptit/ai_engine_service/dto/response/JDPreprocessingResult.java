package com.ptit.ai_engine_service.dto.response;

import java.util.UUID;

public class JDPreprocessingResult {
    UUID jdId;
    String structuredContent;
    boolean success;
    String errorMessage;

    // constructor, getters, setters, and other methods can be added here
    public JDPreprocessingResult(UUID jdId, String structuredContent, boolean success, String errorMessage) {
        this.jdId = jdId;
        this.structuredContent = structuredContent;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}