package com.ptit.ai_engine_service.service.preprocessing.jd;

import com.ptit.ai_engine_service.dto.request.JDPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingData;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingResult;
import com.ptit.ai_engine_service.service.embedding.EmbeddingService;
import com.ptit.ai_engine_service.service.extraction.jd.JDExtractionService;
import com.ptit.ai_engine_service.service.normalization.jd.JDNormalizationService;
import com.ptit.ai_engine_service.vector.service.JDVectorService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JDPreprocessingService {

        private final JDExtractionService extractionService;
        private final JDNormalizationService normalizationService;
        private final EmbeddingService embeddingService;
        private final JDVectorService jdVectorService;

        public JDPreprocessingService(
                        JDExtractionService extractionService,
                        JDNormalizationService normalizationService,
                        EmbeddingService embeddingService,
                        JDVectorService jdVectorService) {

                this.extractionService = extractionService;
                this.normalizationService = normalizationService;
                this.embeddingService = embeddingService;
                this.jdVectorService = jdVectorService;
        }

        public JDPreprocessingResult preprocess(
                        JDPreprocessingRequest request) throws Exception {

                if (request == null || request.jdId() == null) {
                        throw new IllegalArgumentException(
                                        "JD request and jdId must not be null.");
                }

                if (request.rawContent() == null || request.rawContent().isBlank()) {
                        throw new IllegalArgumentException(
                                        "JD raw content must not be empty.");
                }

                // Step 1: Extract information using Gemini.
                String extractedJson = extractionService.extract(request.rawContent());

                if (extractedJson == null || extractedJson.isBlank()) {
                        throw new IllegalStateException(
                                        "Gemini returned an empty extraction result.");
                }

                // Step 2: Normalize extracted information.
                JDPreprocessingData preprocessingData = normalizationService.normalize(extractedJson);

                if (preprocessingData == null) {
                        throw new IllegalStateException(
                                        "JD normalization returned an empty result.");
                }

                String jdPosition = preprocessingData.jdPosition();
                String structuredContent = preprocessingData.structuredContent();

                if (structuredContent == null || structuredContent.isBlank()) {
                        throw new IllegalStateException(
                                        "Normalized JD content must not be empty.");
                }

                // Step 3: Prepare documents for embedding.
                List<Document> documents = embeddingService.createJDDocuments(
                                request.jdId(),
                                structuredContent);

                if (documents == null || documents.isEmpty()) {
                        throw new IllegalStateException(
                                        "No embedding documents were generated for JD: "
                                                        + request.jdId());
                }

                // Step 4: Generate embeddings and save them into Qdrant.
                jdVectorService.saveJDVectors(request.jdId(), documents);

                // Step 5: Return result to Core.
                return new JDPreprocessingResult(
                                request.jdId(),
                                jdPosition,
                                structuredContent,
                                true,
                                null);
        }
}