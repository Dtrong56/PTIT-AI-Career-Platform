package com.ptit.ai_engine_service.service.scoring;

import com.ptit.ai_engine_service.dto.request.CVAnalysisRequest;
import com.ptit.ai_engine_service.dto.response.CVAnalysisResult;
import com.ptit.ai_engine_service.service.embedding.EmbeddingService;
import com.ptit.ai_engine_service.service.skillgap.SkillGapService;
import com.ptit.ai_engine_service.vector.service.CVVectorService;
import com.ptit.ai_engine_service.vector.service.JDVectorService;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CVJDAnalysisService {

    private final EmbeddingService embeddingService;
    private final CVVectorService cvVectorService;
    private final JDVectorService jdVectorService;

    private final SimilarityService similarityService;
    private final SkillGapService skillGapService;
    private final MatchScoreCalculator matchScoreCalculator;

    public CVJDAnalysisService(
            EmbeddingService embeddingService,
            CVVectorService cvVectorService,
            JDVectorService jdVectorService,
            SimilarityService similarityService,
            SkillGapService skillGapService,
            MatchScoreCalculator matchScoreCalculator) {

        this.embeddingService = embeddingService;
        this.cvVectorService = cvVectorService;
        this.jdVectorService = jdVectorService;
        this.similarityService = similarityService;
        this.skillGapService = skillGapService;
        this.matchScoreCalculator = matchScoreCalculator;
    }

    public CVAnalysisResult analyze(CVAnalysisRequest request)
            throws Exception {

        validateRequest(request);

        // 1. Create CV documents
        List<Document> cvDocuments = embeddingService.createCVDocuments(
                request.cvId(),
                request.structuredCv());

        // 2. Create JD documents
        List<Document> jdDocuments = embeddingService.createJDDocuments(
                request.jdId(),
                request.structuredJd());

        if (cvDocuments.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid CV documents found.");
        }

        if (jdDocuments.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid JD documents found.");
        }

        // 3. Save embeddings to Qdrant
        cvVectorService.saveCVVectors(
                request.cvId(),
                cvDocuments);

        jdVectorService.saveJDVectors(
                request.jdId(),
                jdDocuments);

        // 4. Build semantic content
        String cvContent = combineDocuments(cvDocuments);
        String jdContent = combineDocuments(jdDocuments);

        // 5. Calculate semantic score
        double semanticScore = similarityService.calculateSemanticScore(
                cvContent,
                jdContent);

        // 6. Analyze skill gaps
        SkillGapService.Result skillResult = skillGapService.analyze(
                request.structuredCv(),
                request.structuredJd());

        double skillScore = skillResult.skillScore();

        // 7. Calculate final match score
        double matchScore = matchScoreCalculator.calculate(
                semanticScore,
                skillScore);

        // 8. Return analysis result
        return new CVAnalysisResult(
                request.analysisId(),
                matchScore,
                semanticScore,
                skillScore,
                skillResult.skillGaps(),
                true,
                null);
    }

    private String combineDocuments(List<Document> documents) {

        return documents.stream()
                .map(Document::getText)
                .filter(text -> text != null && !text.isBlank())
                .reduce((first, second) -> first + "\n" + second)
                .orElse("");
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