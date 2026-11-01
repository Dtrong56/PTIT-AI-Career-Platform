package com.ptit.core_platform_service.dto.analysis;

import java.util.UUID;

public record CreateAnalysisRequest(
                UUID cvId,
                UUID jdId) {
}