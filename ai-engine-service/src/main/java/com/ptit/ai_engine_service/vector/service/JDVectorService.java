
package com.ptit.ai_engine_service.vector.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class JDVectorService {

    // Khai báo các loại nội dung JD mà chúng ta muốn lưu trữ trong vector store
    private static final List<String> JD_CONTENT_TYPES = List.of(
            "OVERVIEW",
            "RESPONSIBILITIES",
            "REQUIREMENTS",
            "SKILLS");

    // Khai báo một biến vectorStore để lưu trữ các vector embeddings của JD
    private final VectorStore vectorStore;

    // Constructor để khởi tạo JDVectorService với một VectorStore được cung cấp
    public JDVectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Lưu trữ các vector embeddings của JD vào vector store.
     * 
     * @param jdId
     * @param documents
     *                  Các bước thực hiện:
     *                  1. Kiểm tra xem jdId có null hay không, nếu null thì ném ra
     *                  IllegalArgumentException.
     *                  2. Xóa các vector embeddings cũ của JD trước khi lưu trữ các
     *                  vector embeddings mới.
     *                  3. Lưu trữ các vector embeddings mới của JD vào vector
     *                  store.
     */
    public void saveJDVectors(
            UUID jdId,
            List<Document> documents) {

        // 1. Kiểm tra xem jdId có null hay không, nếu null thì ném ra
        // IllegalArgumentException
        if (jdId == null) {
            throw new IllegalArgumentException("jdId must not be null.");
        }

        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException(
                    "No documents available for JD embedding: " + jdId);
        }

        // 2. Xóa các vector embeddings cũ của JD trước khi lưu trữ các vector
        // embeddings mới.
        List<String> oldDocumentIds = JD_CONTENT_TYPES.stream()
                .map(contentType -> UUID.nameUUIDFromBytes(
                        (jdId + "_" + contentType)
                                .getBytes(StandardCharsets.UTF_8))
                        .toString())
                .toList();

        vectorStore.delete(oldDocumentIds);

        // 3. Lưu trữ các vector embeddings mới của JD vào vector store.
        vectorStore.add(documents);
    }
}
