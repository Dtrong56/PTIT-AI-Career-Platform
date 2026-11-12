
package com.ptit.ai_engine_service.service.similarity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimilarityService {

    private static final double MIN_COSINE = 0.40;
    private static final double MAX_COSINE = 0.85;

    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper;

    public SimilarityService(
            EmbeddingModel embeddingModel,
            ObjectMapper objectMapper) {
        this.embeddingModel = embeddingModel;
        this.objectMapper = objectMapper;
    }

    public SimilarityResult calculateSimilarity(
            String structuredCv,
            String structuredJd) {
        String cvText = buildCvRepresentativeText(structuredCv);
        String jdText = buildJdRepresentativeText(structuredJd);

        float[] cvEmbedding = embeddingModel.embed(cvText);
        float[] jdEmbedding = embeddingModel.embed(jdText);

        double cosine = cosineSimilarity(cvEmbedding, jdEmbedding);
        double normalized = normalizeSemanticScore(cosine);

        return new SimilarityResult(
                cosine,
                normalized);
    }

    private String buildCvRepresentativeText(String structuredCv) {
        JsonNode root = parseJson(structuredCv);

        List<String> sections = new ArrayList<>();

        addField(sections, "Skills", root.get("skills"));

        addField(sections, "Experience", root.get("experience"));

        addField(sections, "Projects", root.get("projects"));

        addField(sections, "Certificates", root.get("certifications"));

        addField(sections, "Education Major", extractEducationMajors(root));

        return String.join("\n", sections);
    }

    private String buildJdRepresentativeText(String structuredJd) {
        JsonNode root = parseJson(structuredJd);

        List<String> sections = new ArrayList<>();

        addField(sections, "Job Title", root.get("jobTitle"));
        addField(sections, "Required Skills", filterSkills(root, "REQUIRED"));
        addField(sections, "Preferred Skills", filterSkills(root, "PREFERRED"));
        addField(sections, "Responsibilities", root.get("responsibilities"));
        addField(sections, "Qualifications", root.get("qualifications"));

        return String.join("\n", sections);
    }

    private JsonNode parseJson(String json) {
        try {
            if (json == null || json.isBlank()) {
                throw new IllegalArgumentException("Structured JSON must not be empty");
            }

            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot parse structured JSON",
                    e);
        }
    }

    private void addField(
            List<String> sections,
            String label,
            JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        StringBuilder content = new StringBuilder();
        appendText(node, content);

        if (!content.isEmpty()) {
            sections.add(label + ": " + content);
        }
    }

    private void appendText(JsonNode node, StringBuilder output) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isTextual() || node.isNumber() || node.isBoolean()) {
            String value = node.asText().trim();

            if (!value.isEmpty()) {
                if (!output.isEmpty()) {
                    output.append("; ");
                }
                output.append(value);
            }
            return;
        }

        if (node.isArray()) {
            node.forEach(child -> appendText(child, output));
            return;
        }

        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> appendText(entry.getValue(), output));
        }
    }

    private JsonNode extractEducationMajors(JsonNode root) {
        JsonNode education = root.get("education");

        if (education == null || !education.isArray()) {
            return objectMapper.createArrayNode();
        }

        var result = objectMapper.createArrayNode();

        education.forEach(item -> {
            JsonNode major = item.get("major");

            if (major != null && !major.isNull()) {
                result.add(major);
            }
        });

        return result;
    }

    private JsonNode filterSkills(JsonNode root, String skillType) {
        JsonNode skills = root.get("skills");

        var result = objectMapper.createArrayNode();

        if (skills == null || !skills.isArray()) {
            return result;
        }

        skills.forEach(skill -> {
            String type = skill.path("type").asText("");

            if (skillType.equalsIgnoreCase(type)) {
                result.add(skill);
            }
        });

        return result;
    }

    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null
                || vectorA.length == 0
                || vectorA.length != vectorB.length) {
            throw new IllegalArgumentException(
                    "Invalid embedding vectors");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += (double) vectorA[i] * vectorB[i];
            normA += (double) vectorA[i] * vectorA[i];
            normB += (double) vectorB[i] * vectorB[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            throw new IllegalArgumentException(
                    "Embedding vector must not be zero");
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double calculateCosineSimilarity(
            String skillA,
            String skillB) {
        if (skillA == null || skillA.isBlank()
                || skillB == null || skillB.isBlank()) {
            throw new IllegalArgumentException(
                    "Skill text must not be empty");
        }

        float[] vectorA = embeddingModel.embed(skillA);
        float[] vectorB = embeddingModel.embed(skillB);

        return cosineSimilarity(vectorA, vectorB);
    }

    public double normalizeSemanticScore(double cosine) {
        double normalized = (cosine - MIN_COSINE)
                / (MAX_COSINE - MIN_COSINE);

        return Math.max(0.0, Math.min(1.0, normalized));
    }

    public record SimilarityResult(
            double cosineSimilarity,
            double semanticScore) {
    }
}
