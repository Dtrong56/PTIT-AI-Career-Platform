package com.ptit.ai_engine_service.service.preprocessing.jd;

import com.ptit.ai_engine_service.dto.request.JDPreprocessingRequest;
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
        // Khai báo các service cần thiết để xử lý JD
        private final JDExtractionService extractionService;
        private final JDNormalizationService normalizationService;
        private final EmbeddingService embeddingService;
        private final JDVectorService jdVectorService;

        // Constructor để inject các service vào JDPreprocessingService
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

        /**
         * Phương thức preprocess thực hiện các bước xử lý JD từ raw content đến lưu trữ
         * vector.
         *
         * @param request Yêu cầu chứa thông tin JD cần xử lý.
         * @return Kết quả xử lý JD.
         * @throws Exception Nếu có lỗi trong quá trình xử lý.
         *                   0. Kiểm tra tính hợp lệ của request và raw content.
         *                   1. Gọi Gemini để trích xuất thông tin từ raw content.
         *                   2. Chuẩn hóa thông tin đã trích xuất.
         *                   3. Tạo các tài liệu cho việc tạo embedding.
         *                   4. Tạo embedding và lưu trữ vào Qdrant.
         *                   5. Trả về kết quả xử lý cho Core.
         */
        public JDPreprocessingResult preprocess(
                        JDPreprocessingRequest request) throws Exception {

                if (request == null || request.jdId() == null) {
                        throw new IllegalArgumentException(
                                        "JD request and jdId must not be null.");
                }

                if (request.rawContent() == null
                                || request.rawContent().isBlank()) {

                        throw new IllegalArgumentException(
                                        "JD raw content must not be empty.");
                }

                // Step 1: Extract information using Gemini.
                String extractedJson = extractionService.extract(
                                request.rawContent());

                if (extractedJson == null || extractedJson.isBlank()) {
                        throw new IllegalStateException(
                                        "Gemini returned an empty extraction result.");
                }

                // Step 2: Normalize extracted information.
                String structuredContent = normalizationService.normalize(
                                extractedJson);

                // Step 3: Prepare documents for embedding.
                List<Document> documents = embeddingService.createJDDocuments(
                                request.jdId(),
                                structuredContent);

                // Step 4: Generate embeddings and save them into Qdrant.
                jdVectorService.saveJDVectors(
                                request.jdId(),
                                documents);

                // Step 5: Return result to Core.
                return new JDPreprocessingResult(
                                request.jdId(),
                                structuredContent,
                                true,
                                null);
        }
}