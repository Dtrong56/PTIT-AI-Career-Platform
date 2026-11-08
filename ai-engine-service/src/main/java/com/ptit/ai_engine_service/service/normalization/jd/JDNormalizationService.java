
package com.ptit.ai_engine_service.service.normalization.jd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingData;
import com.ptit.ai_engine_service.service.normalization.skill.SkillNormalizationService;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class JDNormalizationService {

    // khai báo các biến cần thiết
    private final ObjectMapper objectMapper;
    private final SkillNormalizationService skillNormalizationService;

    // Constructor để khởi tạo các biến
    public JDNormalizationService(
            ObjectMapper objectMapper,
            SkillNormalizationService skillNormalizationService) {

        this.objectMapper = objectMapper;
        this.skillNormalizationService = skillNormalizationService;
    }

    /**
     * Chuẩn hóa thông tin trả về từ mô tả công việc (JD) đã được trích xuất.
     * 
     * @param extractedJson
     * @return
     * @throws Exception
     */
    public JDPreprocessingData normalize(String extractedJson) throws Exception {

        String json = extractedJson.trim();

        // Handle JSON returned inside Markdown code fences.
        if (json.startsWith("```")) {
            json = json.replaceFirst("^```(?:json)?\\s*", "");
            json = json.replaceFirst("\\s*```$", "");
        }

        JsonNode source = objectMapper.readTree(json);

        // Normalize job position separately from structured content.
        String jdPosition = normalizeJDPosition(
                normalizeText(source.get("jdPosition")));

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

        result.put("location", normalizeText(source.get("location")));
        result.put("salary", normalizeText(source.get("salary")));

        result.set(
                "qualifications",
                normalizeStringArray(source.get("qualifications")));

        String structuredContent = objectMapper.writeValueAsString(result);

        return new JDPreprocessingData(
                jdPosition,
                structuredContent);
    }

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

            String extractedName = normalizeText(skill.get("name"));
            String type = normalizeText(skill.get("type"));
            String extractedCategory = normalizeText(skill.get("category"));

            if (extractedName == null || type == null) {
                continue;
            }

            type = type.toUpperCase(Locale.ROOT);

            if (!type.equals("REQUIRED")
                    && !type.equals("PREFERRED")) {
                continue;
            }

            SkillNormalizationService.NormalizedSkill normalized = skillNormalizationService.normalize(
                    extractedName,
                    extractedCategory);

            if (normalized == null) {
                continue;
            }

            String key = normalized.name().toLowerCase(Locale.ROOT);

            if (!uniqueSkills.add(key)) {

                // REQUIRED takes precedence over PREFERRED.
                for (JsonNode existingSkill : result) {

                    if (existingSkill.path("name").asText("")
                            .equalsIgnoreCase(normalized.name())) {

                        if (type.equals("REQUIRED")) {
                            ((ObjectNode) existingSkill).put("type", "REQUIRED");
                        }

                        break;
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

        return result;
    }

    private String normalizeText(JsonNode node) {

        if (node == null || node.isNull() || !node.isValueNode()) {
            return null;
        }

        String value = node.asText().trim().replaceAll("\\s+", " ");

        return value.isBlank() ? null : value;
    }
}
