package com.ptit.ai_engine_service.dto.response;

import java.util.UUID;

public record JDPreprocessingResult(
                UUID jdId,
                String jdPosition,
                String structuredContent,
                boolean success,
                String errorMessage) {
}