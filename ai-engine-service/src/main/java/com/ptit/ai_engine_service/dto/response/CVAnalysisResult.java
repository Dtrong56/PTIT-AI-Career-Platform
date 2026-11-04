package com.ptit.ai_engine_service.dto.response;

import java.util.UUID;
import java.util.List;

public class CVAnalysisResult {
    UUID analysisId;
    Double matchScore;
    Double semanticScore;
    Double skillScore;
    List<SkillGapResult> skillGaps;
    boolean success;
    String errorMessage;

    // constructor, getters, setters, etc.
    public CVAnalysisResult(UUID analysisId, Double matchScore, Double semanticScore, Double skillScore,
            List<SkillGapResult> skillGaps, boolean success, String errorMessage) {
        this.analysisId = analysisId;
        this.matchScore = matchScore;
        this.semanticScore = semanticScore;
        this.skillScore = skillScore;
        this.skillGaps = skillGaps;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}