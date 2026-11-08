package com.ptit.ai_engine_service.vector.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class JDVectorService {

        // Các content type mới và cũ cần được dọn dẹp.
        private static final List<String> JD_CONTENT_TYPES = List.of(
                        "OVERVIEW",
                        "RESPONSIBILITIES",
                        "REQUIREMENTS",
                        "QUALIFICATIONS",
                        "SKILLS");

        private final VectorStore vectorStore;

        public JDVectorService(VectorStore vectorStore) {
                this.vectorStore = vectorStore;
        }

        /**
         * Lưu các vector embeddings của JD.
         *
         * 1. Kiểm tra dữ liệu đầu vào.
         * 2. Xóa vector cũ theo cả ID convention cũ và mới.
         * 3. Lưu các vector mới.
         */
        public void saveJDVectors(
                        UUID jdId,
                        List<Document> documents) {

                if (jdId == null) {
                        throw new IllegalArgumentException(
                                        "jdId must not be null.");
                }

                if (documents == null || documents.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "No documents available for JD embedding: " + jdId);
                }

                List<String> oldDocumentIds = new ArrayList<>();

                for (String contentType : JD_CONTENT_TYPES) {

                        // ID convention cũ:
                        // jdId + "_" + contentType
                        String legacyId = UUID.nameUUIDFromBytes(
                                        (jdId + "_" + contentType)
                                                        .getBytes(StandardCharsets.UTF_8))
                                        .toString();

                        // ID convention mới:
                        // JD + "_" + jdId + "_" + contentType
                        String currentId = UUID.nameUUIDFromBytes(
                                        ("JD_" + jdId + "_" + contentType)
                                                        .getBytes(StandardCharsets.UTF_8))
                                        .toString();

                        oldDocumentIds.add(legacyId);
                        oldDocumentIds.add(currentId);
                }

                // Xóa các vector cũ trước khi thêm dữ liệu mới.
                vectorStore.delete(oldDocumentIds);

                // Lưu các vector mới.
                vectorStore.add(documents);
        }
}