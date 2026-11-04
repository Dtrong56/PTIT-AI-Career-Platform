package com.ptit.ai_engine_service.dto.request;

import java.util.UUID;

public class CVPreprocessingRequest {
    UUID cvId;
    UUID studentId;
    String fileName;
    String contentType;
    byte[] fileData;

    // methods
    public UUID cvId() {
        return cvId;
    }
}