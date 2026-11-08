
package com.ptit.ai_engine_service.service.normalization.cv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptit.ai_engine_service.service.normalization.skill.SkillNormalizationService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class CVNormalizationService {

    private final ObjectMapper objectMapper;
    private final SkillNormalizationService skillNormalizationService;

    // private static final Set<String> VALID_SKILL_CATEGORIES = Set.of(
    // "PROGRAMMING_LANGUAGE",
    // "FRAMEWORK",
    // "DATABASE",
    // "TOOL",
    // "PLATFORM",
    // "RUNTIME",
    // "SOFT_SKILL",
    // "OTHER");

    public CVNormalizationService(
            ObjectMapper objectMapper,
            SkillNormalizationService skillNormalizationService) {
        this.objectMapper = objectMapper;
        this.skillNormalizationService = skillNormalizationService;
    }

    public String normalize(String structuredJson) {
        if (structuredJson == null || structuredJson.isBlank()) {
            throw new IllegalArgumentException(
                    "Structured CV JSON must not be empty.");
        }

        try {
            String cleanedJson = removeMarkdownFence(structuredJson);

            JsonNode source = objectMapper.readTree(cleanedJson);

            if (source == null || !source.isObject()) {
                throw new IllegalArgumentException(
                        "Structured CV must be a JSON object.");
            }

            ObjectNode result = objectMapper.createObjectNode();

            result.set("personalInfo", normalizePersonalInfo(source));
            result.put("summary", normalizeText(source.path("summary")));

            result.set("education", normalizeEducation(source));
            result.set("experience", normalizeExperience(source));
            result.set("projects", normalizeProjects(source));
            result.set("skills", normalizeSkills(source));
            result.set("certifications", normalizeCertifications(source));
            result.set("languages", normalizeLanguages(source));

            validateNormalizedData(result);

            return objectMapper.writeValueAsString(result);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to normalize CV data: " + e.getMessage(),
                    e);
        }
    }

    private String removeMarkdownFence(String json) {
        String cleaned = json.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }

    // --------------------------------------------------
    // PERSONAL INFORMATION
    // --------------------------------------------------

    private ObjectNode normalizePersonalInfo(JsonNode source) {
        JsonNode personalInfo = source.path("personalInfo");

        ObjectNode result = objectMapper.createObjectNode();

        result.put("fullName", normalizeText(personalInfo.path("fullName")));
        result.put("email", normalizeText(personalInfo.path("email")));
        result.put("phone", normalizeText(personalInfo.path("phone")));
        result.put("address", normalizeText(personalInfo.path("address")));
        result.put("linkedin", normalizeText(personalInfo.path("linkedin")));
        result.put("github", normalizeText(personalInfo.path("github")));
        result.put("portfolio", normalizeText(personalInfo.path("portfolio")));

        return result;
    }

    // --------------------------------------------------
    // EDUCATION
    // --------------------------------------------------

    private ArrayNode normalizeEducation(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode education = source.path("education");

        if (!education.isArray()) {
            return result;
        }

        for (JsonNode item : education) {
            if (!item.isObject()) {
                continue;
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("school", normalizeText(item.path("school")));
            normalized.put("degree", normalizeText(item.path("degree")));
            normalized.put("major", normalizeText(item.path("major")));
            normalized.put("startDate", normalizeText(item.path("startDate")));
            normalized.put("endDate", normalizeText(item.path("endDate")));
            normalized.put("gpa", normalizeText(item.path("gpa")));

            normalized.set(
                    "details",
                    normalizeStringArray(item.path("details")));

            result.add(normalized);
        }

        return result;
    }

    // --------------------------------------------------
    // EXPERIENCE
    // --------------------------------------------------

    private ArrayNode normalizeExperience(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode experiences = source.path("experience");

        if (!experiences.isArray()) {
            return result;
        }

        for (JsonNode item : experiences) {
            if (!item.isObject()) {
                continue;
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("company", normalizeText(item.path("company")));
            normalized.put("position", normalizeText(item.path("position")));
            normalized.put(
                    "employmentType",
                    normalizeText(item.path("employmentType")));
            normalized.put("startDate", normalizeText(item.path("startDate")));
            normalized.put("endDate", normalizeText(item.path("endDate")));

            // Giữ nguyên nội dung mô tả đã được làm sạch về khoảng trắng.
            // Không thay thế công nghệ trong mô tả bằng tên chuẩn hóa.
            normalized.set(
                    "description",
                    normalizeStringArray(item.path("description")));

            // Chuẩn hóa danh sách công nghệ được trích xuất riêng.
            normalized.set(
                    "technologies",
                    normalizeStringArray(item.path("technologies")));

            result.add(normalized);
        }

        return result;
    }

    // --------------------------------------------------
    // PROJECTS
    // --------------------------------------------------

    private ArrayNode normalizeProjects(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode projects = source.path("projects");

        if (!projects.isArray()) {
            return result;
        }

        for (JsonNode item : projects) {
            if (!item.isObject()) {
                continue;
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("name", normalizeText(item.path("name")));
            normalized.put("role", normalizeText(item.path("role")));
            normalized.put("startDate", normalizeText(item.path("startDate")));
            normalized.put("endDate", normalizeText(item.path("endDate")));
            normalized.put("url", normalizeText(item.path("url")));

            // Không tóm tắt hoặc xóa bằng chứng kỹ thuật trong mô tả.
            normalized.set(
                    "description",
                    normalizeStringArray(item.path("description")));

            normalized.set(
                    "technologies",
                    normalizeStringArray(item.path("technologies")));

            result.add(normalized);
        }

        return result;
    }

    // --------------------------------------------------
    // SKILLS
    // --------------------------------------------------

    private ArrayNode normalizeSkills(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode skills = source.path("skills");

        if (!skills.isArray()) {
            return result;
        }

        Set<String> processedNames = new HashSet<>();

        for (JsonNode item : skills) {
            if (!item.isObject()) {
                continue;
            }

            String extractedName = normalizeText(item.path("name"));

            if (extractedName == null) {
                continue;
            }

            String extractedCategory = normalizeCategory(
                    normalizeText(item.path("category")));

            SkillNormalizationService.NormalizedSkill normalizedSkill = skillNormalizationService.normalize(
                    extractedName,
                    extractedCategory);

            if (normalizedSkill == null
                    || normalizedSkill.name() == null
                    || normalizedSkill.name().isBlank()) {
                continue;
            }

            String normalizedName = normalizedSkill.name().trim();

            // Chỉ loại bỏ kỹ năng trùng trong danh sách skills[].
            // Không ảnh hưởng đến technologies[] hoặc description[]
            // trong từng Experience và Project.
            String uniqueKey = normalizedName.toLowerCase(Locale.ROOT);

            if (!processedNames.add(uniqueKey)) {
                continue;
            }

            String category = normalizedSkill.category();

            if (category == null || category.isBlank()) {
                category = "OTHER";
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("name", normalizedName);
            normalized.put("category", category);
            normalized.put("level", normalizeText(item.path("level")));

            result.add(normalized);
        }

        return result;
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "OTHER";
        }

        String normalized = category.trim().toUpperCase(Locale.ROOT);

        // if (!VALID_SKILL_CATEGORIES.contains(normalized)) {
        // return "OTHER";
        // }

        return normalized;
    }

    // --------------------------------------------------
    // CERTIFICATIONS
    // --------------------------------------------------

    private ArrayNode normalizeCertifications(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode certifications = source.path("certifications");

        if (!certifications.isArray()) {
            return result;
        }

        for (JsonNode item : certifications) {
            if (!item.isObject()) {
                continue;
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("name", normalizeText(item.path("name")));
            normalized.put("issuer", normalizeText(item.path("issuer")));
            normalized.put("date", normalizeText(item.path("date")));

            result.add(normalized);
        }

        return result;
    }

    // --------------------------------------------------
    // LANGUAGES
    // --------------------------------------------------

    private ArrayNode normalizeLanguages(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        JsonNode languages = source.path("languages");

        if (!languages.isArray()) {
            return result;
        }

        for (JsonNode item : languages) {
            if (!item.isObject()) {
                continue;
            }

            ObjectNode normalized = objectMapper.createObjectNode();

            normalized.put("name", normalizeText(item.path("name")));
            normalized.put(
                    "proficiency",
                    normalizeText(item.path("proficiency")));

            result.add(normalized);
        }

        return result;
    }

    // --------------------------------------------------
    // COMMON NORMALIZATION
    // --------------------------------------------------

    private ArrayNode normalizeStringArray(JsonNode source) {
        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        Set<String> processedValues = new HashSet<>();

        for (JsonNode item : source) {
            String value = normalizeText(item);

            if (value == null) {
                continue;
            }

            // Chỉ loại bỏ chuỗi trùng chính xác sau khi chuẩn hóa
            // khoảng trắng và không phân biệt chữ hoa/thường.
            String uniqueKey = value.toLowerCase(Locale.ROOT);

            if (processedValues.add(uniqueKey)) {
                result.add(value);
            }
        }

        return result;
    }

    private String normalizeText(JsonNode node) {
        if (node == null || node.isNull() || !node.isValueNode()) {
            return null;
        }

        String value = node.asText();

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    // --------------------------------------------------
    // OUTPUT VALIDATION
    // --------------------------------------------------

    private void validateNormalizedData(ObjectNode data) {
        validateArray(data, "education");
        validateArray(data, "experience");
        validateArray(data, "projects");
        validateArray(data, "skills");
        validateArray(data, "certifications");
        validateArray(data, "languages");

        validateObjectArray(data, "education");
        validateObjectArray(data, "experience");
        validateObjectArray(data, "projects");
        validateObjectArray(data, "skills");
        validateObjectArray(data, "certifications");
        validateObjectArray(data, "languages");

        validateNestedStringArray(data, "education", "details");

        validateNestedStringArray(data, "experience", "description");
        validateNestedStringArray(data, "experience", "technologies");

        validateNestedStringArray(data, "projects", "description");
        validateNestedStringArray(data, "projects", "technologies");
    }

    private void validateArray(ObjectNode data, String fieldName) {
        if (!data.path(fieldName).isArray()) {
            throw new IllegalStateException(
                    "Invalid normalized CV field: "
                            + fieldName + " must be an array.");
        }
    }

    private void validateObjectArray(ObjectNode data, String fieldName) {
        JsonNode array = data.path(fieldName);

        for (JsonNode item : array) {
            if (!item.isObject()) {
                throw new IllegalStateException(
                        "Invalid item in CV field: " + fieldName);
            }
        }
    }

    private void validateNestedStringArray(
            ObjectNode data,
            String parentField,
            String childField) {
        JsonNode parentArray = data.path(parentField);

        for (JsonNode item : parentArray) {
            JsonNode childArray = item.path(childField);

            if (!childArray.isArray()) {
                throw new IllegalStateException(
                        "Invalid CV field: "
                                + parentField + "." + childField
                                + " must be an array.");
            }

            for (JsonNode value : childArray) {
                if (!value.isTextual()) {
                    throw new IllegalStateException(
                            "Invalid value in CV field: "
                                    + parentField + "." + childField);
                }
            }
        }
    }
}
