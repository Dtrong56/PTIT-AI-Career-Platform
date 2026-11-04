package com.ptit.ai_engine_service.dto.request;

import java.util.UUID;

public class CVAnalysisRequest {
    UUID analysisId;
    UUID cvId;
    UUID jdId;

    // methods
    public UUID analysisId() {
        return analysisId;
    }

    public UUID cvId() {
        return cvId;
    }

    public UUID jdId() {
        return jdId;
    }
}