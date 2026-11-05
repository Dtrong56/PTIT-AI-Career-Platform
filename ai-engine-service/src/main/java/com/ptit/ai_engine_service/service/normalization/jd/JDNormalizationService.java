package com.ptit.ai_engine_service.service.normalization.jd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class JDNormalizationService {

    private final ObjectMapper objectMapper;

    public JDNormalizationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String normalize(String extractedJson) throws Exception {

        String json = extractedJson.trim();

        // Xử lý trường hợp Gemini trả về JSON trong Markdown.
        if (json.startsWith("```")) {
            json = json.replaceFirst("^```(?:json)?\\s*", "");
            json = json.replaceFirst("\\s*```$", "");
        }

        JsonNode source = objectMapper.readTree(json);

        ObjectNode result = objectMapper.createObjectNode();

        result.put("jobTitle", normalizeText(source.get("jobTitle")));
        result.put("company", normalizeText(source.get("company")));
        result.put("experience", normalizeText(source.get("experience")));

        result.set(
                "responsibilities",
                normalizeStringArray(source.get("responsibilities")));

        result.set(
                "requirements",
                normalizeStringArray(source.get("requirements")));

        result.set(
                "benefits",
                normalizeStringArray(source.get("benefits")));

        result.set("skills", normalizeSkills(source.get("skills")));

        return objectMapper.writeValueAsString(result);
    }

    private ArrayNode normalizeStringArray(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();
        Set<String> uniqueValues = new HashSet<>();

        if (source == null || !source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            String value = normalizeText(item);

            if (value == null) {
                continue;
            }

            String key = value.toLowerCase(Locale.ROOT);

            if (uniqueValues.add(key)) {
                result.add(value);
            }
        }

        return result;
    }

    private ArrayNode normalizeSkills(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();
        Set<String> uniqueSkills = new HashSet<>();

        if (source == null || !source.isArray()) {
            return result;
        }

        for (JsonNode skill : source) {

            String name = normalizeText(skill.get("name"));
            String type = normalizeText(skill.get("type"));
            String category = normalizeText(skill.get("category"));

            if (name == null || type == null) {
                continue;
            }

            type = type.toUpperCase(Locale.ROOT);

            if (!type.equals("REQUIRED")
                    && !type.equals("PREFERRED")) {
                continue;
            }

            name = normalizeSkillName(name);

            String key = name.toLowerCase(Locale.ROOT);

            if (!uniqueSkills.add(key)) {
                continue;
            }

            ObjectNode normalizedSkill = objectMapper.createObjectNode();

            normalizedSkill.put("name", name);
            normalizedSkill.put("type", type);
            normalizedSkill.put(
                    "category",
                    category == null ? "OTHER" : category.toUpperCase(Locale.ROOT));

            result.add(normalizedSkill);
        }

        return result;
    }

    private String normalizeSkillName(String name) {

        String normalized = name.trim().replaceAll("\\s+", " ");

        return switch (normalized.toLowerCase(Locale.ROOT)) {
            case "springboot" -> "Spring Boot";
            case "postgres sql", "postgresql database" -> "PostgreSQL";
            case "javascript" -> "JavaScript";
            case "nodejs", "node js" -> "Node.js";
            default -> normalized;
        };
    }

    private String normalizeText(JsonNode node) {

        if (node == null || node.isNull() || !node.isValueNode()) {
            return null;
        }

        String value = node.asText().trim().replaceAll("\\s+", " ");

        return value.isBlank() ? null : value;
    }
}