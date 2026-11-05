
package com.ptit.ai_engine_service.service.preprocessing.cv;

import com.ptit.ai_engine_service.dto.request.CVPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.CVPreprocessingResult;
import com.ptit.ai_engine_service.service.embedding.EmbeddingService;
import com.ptit.ai_engine_service.service.extraction.cv.CVExtractionService;
import com.ptit.ai_engine_service.service.normalization.cv.CVNormalizationService;
import com.ptit.ai_engine_service.service.structuring.cv.CVStructuringService;
import com.ptit.ai_engine_service.vector.service.CVVectorService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CVPreprocessingService {

        // Các service phụ thuộc để thực hiện các bước trong pipeline xử lý CV.
        private final CVExtractionService extractionService;
        private final CVStructuringService structuringService;
        private final CVNormalizationService normalizationService;
        private final EmbeddingService embeddingService;
        private final CVVectorService cvVectorService;

        // Constructor injection để nhận các service phụ thuộc từ Spring context.
        public CVPreprocessingService(
                        CVExtractionService extractionService,
                        CVStructuringService structuringService,
                        CVNormalizationService normalizationService,
                        EmbeddingService embeddingService,
                        CVVectorService cvVectorService) {

                this.extractionService = extractionService;
                this.structuringService = structuringService;
                this.normalizationService = normalizationService;
                this.embeddingService = embeddingService;
                this.cvVectorService = cvVectorService;
        }

        /**
         * CV preprocessing pipeline:
         * 1. Extract text from PDF.
         * 2. Structure CV information using Gemini.
         * 3. Normalize structured CV data.
         * 4. Create semantic documents.
         * 5. Generate embeddings and save vectors to Qdrant.
         * 6. Return preprocessing result to Core.
         */
        public CVPreprocessingResult preprocess(
                        CVPreprocessingRequest request) throws Exception {

                if (request == null || request.cvId() == null) {
                        throw new IllegalArgumentException(
                                        "CV request and cvId must not be null.");
                }

                if (request.fileData() == null || request.fileData().length == 0) {
                        throw new IllegalArgumentException(
                                        "CV PDF data must not be empty.");
                }

                // Step 1: Extract text from PDF.
                String rawText = extractionService.extract(request.fileData());

                // Step 2: Structure CV information using Gemini.
                String extractedJson = structuringService.structure(rawText);

                if (extractedJson == null || extractedJson.isBlank()) {
                        throw new IllegalStateException(
                                        "Gemini returned an empty CV structuring result.");
                }

                // Step 3: Normalize structured CV data.
                String structuredData = normalizationService.normalize(extractedJson);

                if (structuredData == null || structuredData.isBlank()) {
                        throw new IllegalStateException(
                                        "CV normalization returned an empty result.");
                }

                // Step 4: Create semantic documents from normalized CV data.
                List<Document> documents = embeddingService.createCVDocuments(
                                request.cvId(),
                                structuredData);

                if (documents.isEmpty()) {
                        throw new IllegalStateException(
                                        "No semantic content found in the processed CV.");
                }

                // Step 5: Generate embeddings and save vectors to Qdrant.
                cvVectorService.saveCVVectors(
                                request.cvId(),
                                documents);

                // Step 6: Return success only after Qdrant storage succeeds.
                return new CVPreprocessingResult(
                                request.cvId(),
                                rawText,
                                structuredData,
                                true,
                                null);
        }
}
