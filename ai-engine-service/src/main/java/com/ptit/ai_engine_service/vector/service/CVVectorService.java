
package com.ptit.ai_engine_service.vector.service;

import com.ptit.ai_engine_service.service.embedding.EmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CVVectorService {

    // Danh sách các loại nội dung của CV để tạo ID ổn định.
    private static final List<String> CV_CONTENT_TYPES = List.of(
            "SUMMARY",
            "EDUCATION",
            "EXPERIENCE",
            "PROJECTS",
            "SKILLS",
            "CERTIFICATIONS",
            "LANGUAGES");

    // VectorStore được sử dụng để lưu trữ và truy xuất các vector embeddings của
    // CV.
    private final VectorStore vectorStore;

    // Constructor injection để nhận VectorStore từ Spring context.
    public CVVectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Cập nhật toàn bộ vector của CV.
     * Xóa cả những phân đoạn cũ không còn nội dung trong lần xử lý mới.
     * 
     * @param cvId      ID của CV cần cập nhật vector.
     * @param documents Danh sách các Document mới được tạo từ nội dung CV.
     * @return void
     *         Các bước thực hiện:
     *         1. Kiểm tra cvId và documents không null hoặc rỗng.
     *         2. Tạo danh sách các ID cũ dựa trên cvId và các loại nội dung.
     *         3. Xóa các vector cũ khỏi VectorStore.
     *         4. Thêm các Document mới vào VectorStore, Spring AI sẽ tự động
     *         5. tạo embeddings và lưu trữ chúng trong Qdrant.
     */
    public void saveCVVectors(UUID cvId, List<Document> documents) {

        // 1. Kiểm tra cvId và documents không null hoặc rỗng.
        if (cvId == null) {
            throw new IllegalArgumentException("cvId must not be null.");
        }

        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException(
                    "No documents available for CV embedding: " + cvId);
        }

        // 2. Tạo danh sách các ID cũ dựa trên cvId và các loại nội dung.
        List<String> oldDocumentIds = new ArrayList<>();

        for (String contentType : CV_CONTENT_TYPES) {
            oldDocumentIds.add(EmbeddingServiceDocumentId.create(cvId, contentType));
        }

        // 3. Xóa các vector cũ khỏi VectorStore.
        vectorStore.delete(oldDocumentIds);

        // 4. Thêm các Document mới vào VectorStore, Spring AI sẽ tự động
        // 5. tạo embeddings và lưu trữ chúng trong Qdrant.
        vectorStore.add(documents);
    }

    /**
     * Tách logic tạo ID để đảm bảo cùng quy tắc với EmbeddingService.
     */
    private static class EmbeddingServiceDocumentId {

        /**
         * Tạo ID ổn định cho mỗi phân đoạn nội dung của CV dựa trên cvId và
         * contentType.
         * Sử dụng UUID.nameUUIDFromBytes để đảm bảo ID là duy nhất và ổn định.
         * 
         * @param cvId
         * @param contentType
         * @return
         */
        private static String create(UUID cvId, String contentType) {
            return UUID.nameUUIDFromBytes(
                    (cvId + "_" + contentType)
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8))
                    .toString();
        }
    }
}
