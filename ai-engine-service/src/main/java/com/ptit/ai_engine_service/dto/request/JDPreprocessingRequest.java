package com.ptit.ai_engine_service.dto.request;

import java.util.UUID;

public class JDPreprocessingRequest {
    UUID jdId;
    String rawContent;

    public UUID jdId() {
        return jdId;
    }

    public String rawContent() {
        return rawContent;
    }

}