package com.ptit.ai_engine_service.dto.request;

import java.util.UUID;

public record CVAnalysisRequest(
        UUID analysisId,
        UUID cvId,
        UUID jdId,
        String structuredCv,
        String structuredJd) {
}