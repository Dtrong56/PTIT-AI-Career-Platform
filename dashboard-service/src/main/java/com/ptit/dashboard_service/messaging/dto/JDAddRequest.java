package com.ptit.dashboard_service.messaging.dto;

import java.util.UUID;

public record JDAddRequest(
        UUID jdId,
        UUID studentId) {
}