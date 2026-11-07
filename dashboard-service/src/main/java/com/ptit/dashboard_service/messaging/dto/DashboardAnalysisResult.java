package com.ptit.dashboard_service.messaging.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DashboardAnalysisResult(
        UUID analysisId,
        UUID studentId,
        UUID cvId,
        UUID jdId,
        String jdPosition,
        String status,
        Double matchScore,
        Double semanticScore,
        Double skillScore,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        List<SkillGapItem> skillGaps) {

    public record SkillGapItem(
            String skillName) {
    }
}