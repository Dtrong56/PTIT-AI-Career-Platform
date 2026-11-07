package com.ptit.core_platform_service.messaging;

import java.util.UUID;

public record JDPreprocessingResult(
                UUID jdId,
                String jdPosition,
                String structuredContent,
                boolean success,
                String errorMessage) {
}