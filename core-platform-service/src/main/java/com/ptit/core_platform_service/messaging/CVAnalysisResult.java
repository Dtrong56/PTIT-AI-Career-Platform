package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.entity.enums.MatchStatus;
import com.ptit.core_platform_service.entity.enums.SkillType;

import java.util.List;
import java.util.UUID;

public record CVAnalysisResult(
        UUID analysisId,
        Double matchScore,
        Double semanticScore,
        Double skillScore,
        List<SkillGapResult> skillGaps,
        boolean success,
        String errorMessage
) {
}