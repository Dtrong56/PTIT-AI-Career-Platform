
package com.ptit.ai_engine_service.service.normalization.jd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingData;
import com.ptit.ai_engine_service.service.normalization.skill.SkillNormalizationService;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class JDNormalizationService {

    private static final Set<String> VALID_SKILL_TYPES = Set.of("REQUIRED", "PREFERRED");

    private static final List<String> LIST_FIELDS = List.of(
            "responsibilities", "requirements", "qualifications", "benefits");

    private static final List<String> SCALAR_FIELDS = List.of(
            "jobTitle", "company", "location", "salary", "experience");

    private static final List<String> INPUT_SCALAR_FIELDS = List.of(
            "jdPosition", "jobTitle", "company", "location", "salary", "experience");

    private final ObjectMapper objectMapper;
    private final SkillNormalizationService skillNormalizationService;
    private static final Logger log = LoggerFactory.getLogger(JDNormalizationService.class);

    public JDNormalizationService(
            ObjectMapper objectMapper,
            SkillNormalizationService skillNormalizationService) {

        this.objectMapper = objectMapper;
        this.skillNormalizationService = skillNormalizationService;
    }

    public JDPreprocessingData normalize(String extractedJson) {

        if (extractedJson == null || extractedJson.isBlank()) {
            throw new IllegalArgumentException(
                    "Extracted JD JSON must not be empty.");
        }

        JsonNode source = parse(removeMarkdownFence(extractedJson));

        validateSource(source);

        String jdPosition = normalizeJDPosition(
                normalizeText(source.get("jdPosition")));

        ObjectNode result = objectMapper.createObjectNode();

        result.put("jobTitle", normalizeText(source.get("jobTitle")));
        result.put("company", normalizeText(source.get("company")));
        result.put("location", normalizeText(source.get("location")));
        result.put("salary", normalizeText(source.get("salary")));
        result.put("experience", normalizeText(source.get("experience")));

        result.set("responsibilities", normalizeStringArray(source.get("responsibilities")));
        result.set("requirements", normalizeStringArray(source.get("requirements")));
        result.set("qualifications", normalizeStringArray(source.get("qualifications")));
        result.set("benefits", normalizeStringArray(source.get("benefits")));
        result.set("skills", normalizeSkills(source.get("skills")));

        validateNormalizedData(result);

        try {
            return new JDPreprocessingData(
                    jdPosition,
                    objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize normalized JD: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------
    // INPUT VALIDATION
    // --------------------------------------------------

    private String removeMarkdownFence(String json) {
        String cleaned = json.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```(?:json)?\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }

        return cleaned.trim();
    }

    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Extracted JD is not valid JSON: " + e.getOriginalMessage(), e);
        }
    }

    private void validateSource(JsonNode source) {
        if (source == null || !source.isObject()) {
            throw new IllegalArgumentException(
                    "Extracted JD must be a JSON object.");
        }

        for (String field : INPUT_SCALAR_FIELDS) {
            requireScalarIfPresent(source, field);
        }

        requireArrayIfPresent(source, "skills");
        for (String field : LIST_FIELDS) {
            requireArrayIfPresent(source, field);
        }
    }

    private void requireArrayIfPresent(JsonNode source, String field) {
        JsonNode node = source.get(field);

        if (node != null && !node.isNull() && !node.isArray()) {
            throw new IllegalArgumentException(
                    "Extracted JD field '" + field + "' must be an array.");
        }
    }

    private void requireScalarIfPresent(JsonNode source, String field) {
        JsonNode node = source.get(field);

        if (node != null && !node.isNull() && !node.isValueNode()) {
            throw new IllegalArgumentException(
                    "Extracted JD field '" + field + "' must be a scalar value.");
        }
    }

    // --------------------------------------------------
    // NORMALIZATION
    // --------------------------------------------------

    /**
     * Chuẩn hóa vị trí jd
     * 
     * @param position
     * @return
     */
    private String normalizeJDPosition(String position) {

        if (position == null) {
            return null;
        }

        String normalized = position
                .trim()
                .replaceAll("\\s+", " ");

        String key = normalized.toLowerCase(Locale.ROOT);

        return switch (key) {
            case "backend",
                    "backend developer",
                    "backend engineer",
                    "back-end developer",
                    "back end developer" ->
                "Backend Developer";

            case "frontend",
                    "frontend developer",
                    "frontend engineer",
                    "front-end developer",
                    "front end developer" ->
                "Frontend Developer";

            case "fullstack",
                    "full stack developer",
                    "full-stack developer",
                    "fullstack developer" ->
                "Full Stack Developer";

            case "business analyst",
                    "ba" ->
                "Business Analyst";

            case "qa",
                    "qc",
                    "qa/qc",
                    "qa/qc engineer",
                    "quality assurance engineer",
                    "software tester",
                    "manual tester",
                    "automation tester" ->
                "QA/QC Engineer";

            case "data analyst" ->
                "Data Analyst";

            case "data engineer" ->
                "Data Engineer";

            case "devops",
                    "devops engineer" ->
                "DevOps Engineer";

            case "ui/ux designer",
                    "ui ux designer",
                    "ux/ui designer" ->
                "UI/UX Designer";

            case "mobile developer",
                    "mobile app developer",
                    "mobile engineer" ->
                "Mobile Developer";

            default -> normalized;
        };
    }

    private ArrayNode normalizeStringArray(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();
        Set<String> uniqueValues = new HashSet<>();

        if (source == null || !source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {
            String value = normalizeText(item);

            if (value != null && uniqueValues.add(value.toLowerCase(Locale.ROOT))) {
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

        int droppedCount = 0;

        for (JsonNode skill : source) {

            if (!skill.isObject()) {
                droppedCount++;
                log.warn("Dropped JD skill (not an object): {}", skill);
                continue;
            }

            String extractedName = normalizeText(skill.get("name"));
            String type = normalizeText(skill.get("type"));
            String extractedCategory = normalizeText(skill.get("category"));

            if (extractedName == null) {
                droppedCount++;
                log.warn("Dropped JD skill (missing name): {}", skill);
                continue;
            }

            type = type == null ? null : type.toUpperCase(Locale.ROOT);

            if (type == null || !VALID_SKILL_TYPES.contains(type)) {
                droppedCount++;
                log.warn("Dropped JD skill '{}' (invalid type: {})", extractedName, type);
                continue;
            }

            SkillNormalizationService.NormalizedSkill normalized = skillNormalizationService.normalize(extractedName,
                    extractedCategory);

            if (normalized == null
                    || normalized.name() == null
                    || normalized.name().isBlank()) {
                droppedCount++;
                continue;
            }

            String key = normalized.name().toLowerCase(Locale.ROOT);

            if (!uniqueSkills.add(key)) {
                // REQUIRED được ưu tiên hơn PREFERRED.
                if (type.equals("REQUIRED")) {
                    for (JsonNode existing : result) {
                        if (existing.path("name").asText("")
                                .equalsIgnoreCase(normalized.name())) {
                            ((ObjectNode) existing).put("type", "REQUIRED");
                            break;
                        }
                    }
                }
                continue;
            }

            ObjectNode normalizedSkill = objectMapper.createObjectNode();
            normalizedSkill.put("name", normalized.name());
            normalizedSkill.put("type", type);
            normalizedSkill.put("category", normalized.category());

            result.add(normalizedSkill);
        }

        if (droppedCount > 0) {
            log.warn("JD normalization dropped {}/{} invalid skills", droppedCount, source.size());
        }

        // Có skills trong đầu vào nhưng không skill nào dùng được: extraction đã sai
        // schema.
        if (!source.isEmpty() && result.isEmpty()) {
            throw new IllegalArgumentException(
                    "None of the " + source.size()
                            + " extracted JD skills are valid. Re-check the extraction output.");
        }

        return result;
    }

    private String normalizeText(JsonNode node) {
        if (node == null || node.isNull() || !node.isValueNode()) {
            return null;
        }

        String value = node.asText().trim().replaceAll("\\s+", " ");

        return value.isBlank() ? null : value;
    }

    // --------------------------------------------------
    // OUTPUT VALIDATION
    // --------------------------------------------------

    private void validateNormalizedData(ObjectNode data) {

        for (String field : SCALAR_FIELDS) {
            if (!data.has(field)) {
                throw new IllegalStateException(
                        "Invalid normalized JD: missing field " + field);
            }
        }

        for (String field : LIST_FIELDS) {
            JsonNode array = data.path(field);

            if (!array.isArray()) {
                throw new IllegalStateException(
                        "Invalid normalized JD field: " + field + " must be an array.");
            }

            for (JsonNode value : array) {
                if (!value.isTextual()) {
                    throw new IllegalStateException(
                            "Invalid value in JD field: " + field);
                }
            }
        }

        JsonNode skills = data.path("skills");

        if (!skills.isArray()) {
            throw new IllegalStateException(
                    "Invalid normalized JD field: skills must be an array.");
        }

        for (JsonNode skill : skills) {
            if (!skill.isObject()
                    || !skill.path("name").isTextual()
                    || skill.path("name").asText().isBlank()) {
                throw new IllegalStateException(
                        "Invalid JD skill: name must not be empty.");
            }

            if (!VALID_SKILL_TYPES.contains(skill.path("type").asText(""))) {
                throw new IllegalStateException(
                        "Invalid JD skill type for: " + skill.path("name").asText());
            }

            if (!skill.path("category").isTextual()
                    || skill.path("category").asText().isBlank()) {
                throw new IllegalStateException(
                        "Invalid JD skill category for: " + skill.path("name").asText());
            }
        }
    }
}