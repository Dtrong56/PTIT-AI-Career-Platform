package com.ptit.core_platform_service.messaging;

import java.util.UUID;

public record CVAnalysisRequest(
        UUID analysisId,
        UUID cvId,
        UUID jdId
) {
}