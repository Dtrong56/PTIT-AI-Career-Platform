package com.ptit.ai_engine_service.service.scoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.ai_engine_service.dto.request.CVAnalysisRequest;
import com.ptit.ai_engine_service.dto.response.CVAnalysisResult;
import com.ptit.ai_engine_service.service.similarity.SimilarityService;
import com.ptit.ai_engine_service.service.skillgap.SkillGapService;
import org.springframework.stereotype.Service;

@Service
public class CVJDAnalysisService {

    private final ObjectMapper objectMapper;
    private final SimilarityService similarityService;
    private final SkillGapService skillGapService;
    private final MatchScoreCalculator matchScoreCalculator;

    public CVJDAnalysisService(
            ObjectMapper objectMapper,
            SimilarityService similarityService,
            SkillGapService skillGapService,
            MatchScoreCalculator matchScoreCalculator) {
        this.objectMapper = objectMapper;
        this.similarityService = similarityService;
        this.skillGapService = skillGapService;
        this.matchScoreCalculator = matchScoreCalculator;
    }

    public CVAnalysisResult analyze(CVAnalysisRequest request)
            throws Exception {

        validateRequest(request);

        // 1. Parse structured JD to determine whether it contains skills.
        JsonNode jd = objectMapper.readTree(request.structuredJd());

        boolean hasSkills = hasValidSkills(jd);

        // 2. Calculate semantic similarity.
        SimilarityService.SimilarityResult similarityResult = similarityService.calculateSimilarity(
                request.structuredCv(),
                request.structuredJd());

        double semanticScore = similarityResult.semanticScore();

        // 3. Analyze skill gaps.
        SkillGapService.Result skillResult = skillGapService.analyze(
                request.structuredCv(),
                request.structuredJd());

        double skillScore = skillResult.skillScore();

        // 4. Calculate final match score.
        double matchScore = matchScoreCalculator.calculate(
                semanticScore,
                skillScore,
                hasSkills);

        // 5. Return analysis result.
        return new CVAnalysisResult(
                request.analysisId(),
                matchScore,
                semanticScore,
                skillScore,
                skillResult.skillGaps(),
                true,
                null);
    }

    private boolean hasValidSkills(JsonNode jd) {

        JsonNode skills = jd.path("skills");

        if (!skills.isArray()) {
            return false;
        }

        for (JsonNode skill : skills) {
            if (skill.path("name").isTextual()
                    && !skill.path("name").asText().isBlank()) {
                return true;
            }
        }

        return false;
    }

    private void validateRequest(CVAnalysisRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Analysis request must not be null.");
        }

        if (request.analysisId() == null
                || request.cvId() == null
                || request.jdId() == null) {

            throw new IllegalArgumentException(
                    "Analysis ID, CV ID and JD ID are required.");
        }

        if (request.structuredCv() == null
                || request.structuredCv().isBlank()) {

            throw new IllegalArgumentException(
                    "Structured CV must not be empty.");
        }

        if (request.structuredJd() == null
                || request.structuredJd().isBlank()) {

            throw new IllegalArgumentException(
                    "Structured JD must not be empty.");
        }
    }
}
