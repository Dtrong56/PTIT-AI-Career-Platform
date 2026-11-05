package com.ptit.ai_engine_service.service.normalization.cv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class CVNormalizationService {

    private final ObjectMapper objectMapper;

    public CVNormalizationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Chuẩn hóa dữ liệu JSON trích xuất từ CV.
     * 
     * @param extractedJson
     * @return
     * @throws Exception
     *                   Các bước chuẩn hóa dữ liệu:
     *                   0. Kiểm tra dữ liệu đầu vào có hợp lệ hay không.
     *                   1. Loại bỏ các ký tự Markdown code fences nếu có.
     *                   2. Kiểm tra dữ liệu JSON có hợp lệ hay không.
     *                   3. Chuẩn hóa các trường dữ liệu trong JSON:
     *                   - personalInfo: Chuẩn hóa các trường thông tin cá nhân.
     *                   - summary: Chuẩn hóa trường tóm tắt.
     *                   - education: Chuẩn hóa danh sách các trường học.
     *                   - experience: Chuẩn hóa danh sách các kinh nghiệm làm việc.
     *                   - projects: Chuẩn hóa danh sách các dự án.
     *                   - skills: Chuẩn hóa danh sách các kỹ năng.
     *                   - certifications: Chuẩn hóa danh sách các chứng chỉ.
     *                   - languages: Chuẩn hóa danh sách các ngôn ngữ.
     *                   4. Trả về dữ liệu JSON đã chuẩn hóa.
     */
    public String normalize(String extractedJson) throws Exception {

        // 0. Kiểm tra dữ liệu đầu vào có hợp lệ hay không.
        if (extractedJson == null || extractedJson.isBlank()) {
            throw new IllegalArgumentException(
                    "CV extracted JSON must not be empty.");
        }

        String json = extractedJson.trim();

        // 1. Loại bỏ các ký tự Markdown code fences nếu có.
        if (json.startsWith("```")) {
            json = json.replaceFirst("^```(?:json)?\\s*", "");
            json = json.replaceFirst("\\s*```$", "");
        }

        // 2. Kiểm tra dữ liệu JSON có hợp lệ hay không.
        JsonNode source = objectMapper.readTree(json);

        if (source == null || !source.isObject()) {
            throw new IllegalArgumentException(
                    "CV extraction result must be a JSON object.");
        }

        // 3. Chuẩn hóa các trường dữ liệu trong JSON.
        ObjectNode result = objectMapper.createObjectNode();

        result.set(
                "personalInfo",
                normalizePersonalInfo(source.path("personalInfo")));

        result.put(
                "summary",
                normalizeText(source.path("summary")));

        result.set(
                "education",
                normalizeEducation(source.path("education")));

        result.set(
                "experience",
                normalizeExperience(source.path("experience")));

        result.set(
                "projects",
                normalizeProjects(source.path("projects")));

        result.set(
                "skills",
                normalizeSkills(source.path("skills")));

        result.set(
                "certifications",
                normalizeCertifications(source.path("certifications")));

        result.set(
                "languages",
                normalizeLanguages(source.path("languages")));

        // 4. Trả về dữ liệu JSON đã chuẩn hóa.
        return objectMapper.writeValueAsString(result);
    }

    /**
     * Chuẩn hóa thông tin cá nhân.
     * 
     * @param source
     * @return
     */
    private ObjectNode normalizePersonalInfo(JsonNode source) {

        ObjectNode result = objectMapper.createObjectNode();

        result.put("fullName", normalizeText(source.path("fullName")));
        result.put("email", normalizeText(source.path("email")));
        result.put("phone", normalizeText(source.path("phone")));
        result.put("address", normalizeText(source.path("address")));
        result.put("linkedin", normalizeText(source.path("linkedin")));
        result.put("github", normalizeText(source.path("github")));
        result.put("portfolio", normalizeText(source.path("portfolio")));

        return result;
    }

    /**
     * Chuẩn hóa danh sách các trường học.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeEducation(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            ObjectNode education = objectMapper.createObjectNode();

            education.put("school", normalizeText(item.path("school")));
            education.put("degree", normalizeText(item.path("degree")));
            education.put("major", normalizeText(item.path("major")));
            education.put("startDate", normalizeText(item.path("startDate")));
            education.put("endDate", normalizeText(item.path("endDate")));
            education.put("gpa", normalizeText(item.path("gpa")));

            education.set(
                    "details",
                    normalizeStringArray(item.path("details")));

            result.add(education);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các kinh nghiệm làm việc.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeExperience(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            ObjectNode experience = objectMapper.createObjectNode();

            experience.put("company", normalizeText(item.path("company")));
            experience.put("position", normalizeText(item.path("position")));
            experience.put(
                    "employmentType",
                    normalizeText(item.path("employmentType")));

            experience.put("startDate", normalizeText(item.path("startDate")));
            experience.put("endDate", normalizeText(item.path("endDate")));

            experience.set(
                    "description",
                    normalizeStringArray(item.path("description")));

            experience.set(
                    "technologies",
                    normalizeStringArray(item.path("technologies")));

            result.add(experience);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các dự án.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeProjects(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            ObjectNode project = objectMapper.createObjectNode();

            project.put("name", normalizeText(item.path("name")));
            project.put("role", normalizeText(item.path("role")));
            project.put("startDate", normalizeText(item.path("startDate")));
            project.put("endDate", normalizeText(item.path("endDate")));
            project.put("url", normalizeText(item.path("url")));

            project.set(
                    "description",
                    normalizeStringArray(item.path("description")));

            project.set(
                    "technologies",
                    normalizeStringArray(item.path("technologies")));

            result.add(project);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các kỹ năng.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeSkills(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();
        Set<String> uniqueSkills = new HashSet<>();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            String name = normalizeText(item.path("name"));

            if (name == null) {
                continue;
            }

            name = normalizeSkillName(name);

            String key = name.toLowerCase(Locale.ROOT);

            if (!uniqueSkills.add(key)) {
                continue;
            }

            String category = normalizeText(item.path("category"));

            if (category == null) {
                category = "OTHER";
            } else {
                category = category.toUpperCase(Locale.ROOT);
            }

            if (!isValidSkillCategory(category)) {
                category = "OTHER";
            }

            ObjectNode skill = objectMapper.createObjectNode();

            skill.put("name", name);
            skill.put("category", category);
            skill.put("level", normalizeText(item.path("level")));

            result.add(skill);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các chứng chỉ.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeCertifications(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            ObjectNode certification = objectMapper.createObjectNode();

            certification.put("name", normalizeText(item.path("name")));
            certification.put("issuer", normalizeText(item.path("issuer")));
            certification.put("date", normalizeText(item.path("date")));

            result.add(certification);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các ngôn ngữ.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeLanguages(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();

        if (!source.isArray()) {
            return result;
        }

        for (JsonNode item : source) {

            ObjectNode language = objectMapper.createObjectNode();

            language.put("name", normalizeText(item.path("name")));
            language.put(
                    "proficiency",
                    normalizeText(item.path("proficiency")));

            result.add(language);
        }

        return result;
    }

    /**
     * Chuẩn hóa danh sách các chuỗi.
     * 
     * @param source
     * @return
     */
    private ArrayNode normalizeStringArray(JsonNode source) {

        ArrayNode result = objectMapper.createArrayNode();
        Set<String> uniqueValues = new HashSet<>();

        if (!source.isArray()) {
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

    /**
     * Chuẩn hóa tên kỹ năng.
     * 
     * @param name
     * @return
     */
    private String normalizeSkillName(String name) {

        String normalized = name.trim().replaceAll("\\s+", " ");

        return switch (normalized.toLowerCase(Locale.ROOT)) {
            case "springboot" -> "Spring Boot";
            case "postgres sql", "postgresql database" -> "PostgreSQL";
            case "nodejs", "node js" -> "Node.js";
            case "reactjs", "react js" -> "React";
            case "vuejs", "vue js" -> "Vue.js";
            default -> normalized;
        };
    }

    /**
     * Kiểm tra xem danh mục kỹ năng có hợp lệ không.
     * 
     * @param category
     * @return
     */
    private boolean isValidSkillCategory(String category) {

        return switch (category) {
            case "PROGRAMMING_LANGUAGE",
                    "FRAMEWORK",
                    "DATABASE",
                    "TOOL",
                    "PLATFORM",
                    "SOFT_SKILL",
                    "OTHER" ->
                true;
            default -> false;
        };
    }

    /**
     * Chuẩn hóa chuỗi văn bản.
     * 
     * @param node
     * @return
     */
    private String normalizeText(JsonNode node) {

        if (node == null || node.isMissingNode()
                || node.isNull() || !node.isValueNode()) {
            return null;
        }

        String value = node.asText()
                .trim()
                .replaceAll("\\s+", " ");

        return value.isBlank() ? null : value;
    }
}