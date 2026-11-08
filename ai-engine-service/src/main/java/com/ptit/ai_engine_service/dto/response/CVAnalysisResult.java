package com.ptit.ai_engine_service.dto.response;

import java.util.List;
import java.util.UUID;

public record CVAnalysisResult(
        UUID analysisId,
        Double matchScore,
        Double semanticScore,
        Double skillScore,
        List<SkillGapResult> skillGaps,
        boolean success,
        String errorMessage) {
}