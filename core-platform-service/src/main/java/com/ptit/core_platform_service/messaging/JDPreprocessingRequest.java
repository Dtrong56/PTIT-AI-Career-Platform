package com.ptit.core_platform_service.messaging;

import java.util.UUID;

public record JDPreprocessingRequest(
        UUID jdId,
        String rawContent
) {
}