package com.ptit.ai_engine_service.service.scoring;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class SimilarityService {

    private final EmbeddingModel embeddingModel;

    public SimilarityService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public double calculateCosineSimilarity(
            String text1,
            String text2) {

        if (text1 == null || text1.isBlank()
                || text2 == null || text2.isBlank()) {
            throw new IllegalArgumentException(
                    "Input text must not be empty.");
        }

        float[] vector1 = embeddingModel.embed(text1);
        float[] vector2 = embeddingModel.embed(text2);

        if (vector1 == null || vector2 == null
                || vector1.length != vector2.length
                || vector1.length == 0) {
            throw new IllegalStateException(
                    "Invalid embedding vectors.");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Chuyển cosine similarity thành thang điểm 0-100.
     */
    public double calculateSemanticScore(
            String cvContent,
            String jdContent) {

        double similarity = calculateCosineSimilarity(
                cvContent,
                jdContent);

        return round(Math.max(0.0, Math.min(1.0, similarity)) * 100);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}