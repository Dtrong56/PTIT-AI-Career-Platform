package com.ptit.ai_engine_service.service.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmbeddingService {

    private final ObjectMapper objectMapper;

    public EmbeddingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Tạo danh sách Document từ JD đã được cấu trúc.
     * 
     * @param jdId
     * @param structuredContent
     * @return
     * @throws Exception
     *                   Các bước thực hiện:
     *                   1. Tạo Document cho phần tổng quan (overview) của JD.
     *                   2. Tạo Document cho phần trách nhiệm (responsibilities) của
     *                   JD.
     *                   3. Tạo Document cho phần yêu cầu (requirements) của JD.
     *                   4. Tạo Document cho phần kỹ năng (skills) của JD.
     *                   5. Loại bỏ các Document có nội dung trống.
     */
    public List<Document> createJDDocuments(
            UUID jdId,
            String structuredContent) throws Exception {

        JsonNode jd = objectMapper.readTree(structuredContent);

        List<Document> documents = new ArrayList<>();

        // 1. General job information
        String overview = buildOverview(jd);

        addDocument(
                documents,
                jdId,
                "OVERVIEW",
                overview);

        // 2. Responsibilities
        addDocument(
                documents,
                jdId,
                "RESPONSIBILITIES",
                buildListContent(
                        "Job responsibilities",
                        jd.path("responsibilities")));

        // 3. Requirements
        addDocument(
                documents,
                jdId,
                "REQUIREMENTS",
                buildListContent(
                        "Job requirements",
                        jd.path("requirements")));

        // 4. Skills
        addDocument(
                documents,
                jdId,
                "SKILLS",
                buildSkillsContent(jd.path("skills")));

        // Remove empty documents.
        documents.removeIf(document -> document.getText() == null
                || document.getText().isBlank());

        return documents;
    }

    /**
     * Tạo nội dung tổng quan (overview) của JD.
     * 
     * @param jd
     * @return
     */
    private String buildOverview(JsonNode jd) {

        StringBuilder content = new StringBuilder();

        appendField(content, "Job title", jd.path("jobTitle"));
        appendField(content, "Company", jd.path("company"));
        appendField(content, "Experience", jd.path("experience"));

        return content.toString();
    }

    /**
     * Tạo nội dung danh sách từ một mảng JSON.
     * 
     * @param title
     * @param items
     * @return
     */
    private String buildListContent(
            String title,
            JsonNode items) {

        StringBuilder content = new StringBuilder();
        content.append(title).append(":\n");

        if (items.isArray()) {
            for (JsonNode item : items) {
                if (item.isTextual() && !item.asText().isBlank()) {
                    content.append("- ")
                            .append(item.asText().trim())
                            .append("\n");
                }
            }
        }

        return content.toString();
    }

    /**
     * Tạo nội dung kỹ năng từ một mảng JSON.
     * 
     * @param skills
     * @return
     */
    private String buildSkillsContent(JsonNode skills) {

        StringBuilder content = new StringBuilder();
        content.append("Job required and preferred skills:\n");

        if (skills.isArray()) {
            for (JsonNode skill : skills) {

                String name = skill.path("name").asText("");
                String type = skill.path("type").asText("");
                String category = skill.path("category").asText("");

                if (name.isBlank()) {
                    continue;
                }

                content.append("- ")
                        .append(name)
                        .append(" | Type: ")
                        .append(type)
                        .append(" | Category: ")
                        .append(category)
                        .append("\n");
            }
        }

        return content.toString();
    }

    /**
     * Thêm một Document vào danh sách Document.
     * 
     * @param documents
     * @param jdId
     * @param contentType
     * @param content
     */
    private void addDocument(
            List<Document> documents,
            UUID jdId,
            String contentType,
            String content) {

        if (content == null || content.isBlank()) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("jdId", jdId.toString());
        metadata.put("contentType", contentType);

        // Stable ID prevents duplicate documents when the same JD
        // is processed again.
        String documentId = UUID.nameUUIDFromBytes(
                (jdId + "_" + contentType)
                        .getBytes(StandardCharsets.UTF_8))
                .toString();

        documents.add(new Document(
                documentId,
                content,
                metadata));
    }

    // ========================= CV DOCUMENTS =========================

    /**
     * Tạo các Document ngữ nghĩa từ CV đã được chuẩn hóa.
     * Không đưa email, số điện thoại, địa chỉ hoặc thông tin liên hệ
     * cá nhân vào vector.
     * 
     * @param cvId
     * @param structuredContent
     * @return
     * @throws Exception
     *                   Các bước thực hiện:
     *                   1. Tạo Document cho phần tóm tắt (summary) của CV.
     *                   2. Tạo Document cho phần giáo dục (education) của CV.
     *                   3. Tạo Document cho phần kinh nghiệm (experience) của CV.
     *                   4. Tạo Document cho phần dự án (projects) của CV.
     *                   5. Tạo Document cho phần kỹ năng (skills) của CV.
     *                   6. Tạo Document cho phần chứng chỉ (certifications) của CV.
     *                   7. Tạo Document cho phần ngôn ngữ (languages) của CV.
     *                   8. Loại bỏ các Document có nội dung trống.
     *                   9. Trả về danh sách Document.
     */
    public List<Document> createCVDocuments(
            UUID cvId,
            String structuredContent) throws Exception {

        // 1. Validate inputs
        if (cvId == null) {
            throw new IllegalArgumentException("cvId must not be null.");
        }

        if (structuredContent == null || structuredContent.isBlank()) {
            throw new IllegalArgumentException("CV structured content must not be empty.");
        }

        // 2. Parse structured content
        JsonNode cv = objectMapper.readTree(structuredContent);
        List<Document> documents = new ArrayList<>();

        addCVDocument(
                documents,
                cvId,
                "SUMMARY",
                buildSummaryContent(cv));

        addCVDocument(
                documents,
                cvId,
                "EDUCATION",
                buildEducationContent(cv.path("education")));

        addCVDocument(
                documents,
                cvId,
                "EXPERIENCE",
                buildExperienceContent(cv.path("experience")));

        addCVDocument(
                documents,
                cvId,
                "PROJECTS",
                buildProjectsContent(cv.path("projects")));

        addCVDocument(
                documents,
                cvId,
                "SKILLS",
                buildCVSkillsContent(cv.path("skills")));

        addCVDocument(
                documents,
                cvId,
                "CERTIFICATIONS",
                buildCertificationsContent(cv.path("certifications")));

        addCVDocument(
                documents,
                cvId,
                "LANGUAGES",
                buildLanguagesContent(cv.path("languages")));

        // 3. Remove empty documents
        documents.removeIf(document -> document.getText() == null || document.getText().isBlank());

        // 4. Return documents
        return documents;
    }

    /**
     * Tạo nội dung tóm tắt từ một JSON.
     * 
     * @param cv
     * @return
     */
    private String buildSummaryContent(JsonNode cv) {
        StringBuilder content = new StringBuilder();

        appendField(content, "Professional summary", cv.path("summary"));

        return content.toString();
    }

    /**
     * Tạo nội dung giáo dục từ một mảng JSON.
     * 
     * @param education
     * @return
     */
    private String buildEducationContent(JsonNode education) {
        StringBuilder content = new StringBuilder();
        content.append("Education:\n");

        if (!education.isArray()) {
            return "";
        }

        for (JsonNode item : education) {
            appendField(content, "School", item.path("school"));
            appendField(content, "Degree", item.path("degree"));
            appendField(content, "Major", item.path("major"));
            appendField(content, "Start date", item.path("startDate"));
            appendField(content, "End date", item.path("endDate"));
            appendField(content, "GPA", item.path("gpa"));
            appendTextArray(content, "Details", item.path("details"));
            content.append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo nội dung kinh nghiệm làm việc từ một mảng JSON.
     * 
     * @param experience
     * @return
     */
    private String buildExperienceContent(JsonNode experience) {
        StringBuilder content = new StringBuilder();
        content.append("Professional experience:\n");

        if (!experience.isArray()) {
            return "";
        }

        for (JsonNode item : experience) {
            appendField(content, "Company", item.path("company"));
            appendField(content, "Position", item.path("position"));
            appendField(content, "Employment type", item.path("employmentType"));
            appendField(content, "Start date", item.path("startDate"));
            appendField(content, "End date", item.path("endDate"));
            appendTextArray(content, "Responsibilities and achievements",
                    item.path("description"));
            appendTextArray(content, "Technologies", item.path("technologies"));
            content.append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo nội dung dự án từ một mảng JSON.
     * 
     * @param projects
     * @return
     */
    private String buildProjectsContent(JsonNode projects) {
        StringBuilder content = new StringBuilder();
        content.append("Projects:\n");

        if (!projects.isArray()) {
            return "";
        }

        for (JsonNode item : projects) {
            appendField(content, "Project name", item.path("name"));
            appendField(content, "Role", item.path("role"));
            appendField(content, "Start date", item.path("startDate"));
            appendField(content, "End date", item.path("endDate"));
            appendTextArray(content, "Project description", item.path("description"));
            appendTextArray(content, "Technologies", item.path("technologies"));
            content.append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo nội dung kỹ năng từ một mảng JSON.
     * 
     * @param skills
     * @return
     */
    private String buildCVSkillsContent(JsonNode skills) {
        StringBuilder content = new StringBuilder();
        content.append("Candidate skills:\n");

        if (!skills.isArray()) {
            return "";
        }

        for (JsonNode skill : skills) {
            String name = skill.path("name").asText("");
            if (name.isBlank()) {
                continue;
            }

            content.append("- ")
                    .append(name.trim())
                    .append(" | Category: ")
                    .append(skill.path("category").asText("OTHER"))
                    .append(" | Level: ")
                    .append(skill.path("level").asText("Not specified"))
                    .append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo nội dung chứng chỉ từ một mảng JSON.
     * 
     * @param certifications
     * @return
     */
    private String buildCertificationsContent(JsonNode certifications) {
        StringBuilder content = new StringBuilder();
        content.append("Certifications:\n");

        if (!certifications.isArray()) {
            return "";
        }

        for (JsonNode item : certifications) {
            appendField(content, "Certification", item.path("name"));
            appendField(content, "Issuer", item.path("issuer"));
            appendField(content, "Date", item.path("date"));
            content.append("\n");
        }

        return content.toString();
    }

    /**
     * Tạo nội dung ngôn ngữ từ một mảng JSON.
     * 
     * @param languages
     * @return
     */
    private String buildLanguagesContent(JsonNode languages) {
        StringBuilder content = new StringBuilder();
        content.append("Languages:\n");

        if (!languages.isArray()) {
            return "";
        }

        for (JsonNode item : languages) {
            appendField(content, "Language", item.path("name"));
            appendField(content, "Proficiency", item.path("proficiency"));
            content.append("\n");
        }

        return content.toString();
    }

    /**
     * Thêm một mảng văn bản vào nội dung tổng quan (overview) của JD.
     * 
     * @param content
     * @param label
     * @param values
     */
    private void appendTextArray(
            StringBuilder content,
            String label,
            JsonNode values) {

        if (!values.isArray() || values.isEmpty()) {
            return;
        }

        content.append(label).append(":\n");

        for (JsonNode value : values) {
            if (value.isTextual() && !value.asText().isBlank()) {
                content.append("- ")
                        .append(value.asText().trim())
                        .append("\n");
            }
        }
    }

    /**
     * Thêm một Document cho CV vào danh sách Document.
     * 
     * @param documents
     * @param cvId
     * @param contentType
     * @param content
     */
    private void addCVDocument(
            List<Document> documents,
            UUID cvId,
            String contentType,
            String content) {

        if (content == null || content.isBlank()) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("documentType", "CV");
        metadata.put("cvId", cvId.toString());
        metadata.put("contentType", contentType);

        String documentId = createDocumentId(cvId, contentType);

        documents.add(new Document(documentId, content, metadata));
    }

    // ========================= SHARED HELPERS =========================
    /**
     * Thêm một trường vào nội dung tổng quan (overview) của JD.
     * 
     * @param content
     * @param label
     * @param value
     */
    private void appendField(
            StringBuilder content,
            String label,
            JsonNode value) {

        if (value != null
                && !value.isMissingNode()
                && !value.isNull()
                && !value.asText().isBlank()) {

            content.append(label)
                    .append(": ")
                    .append(value.asText().trim())
                    .append("\n");
        }
    }

    /**
     * Tạo một ID duy nhất cho Document dựa trên entityId và contentType.
     * 
     * @param entityId
     * @param contentType
     * @return
     */
    private String createDocumentId(UUID entityId, String contentType) {
        return UUID.nameUUIDFromBytes(
                (entityId + "_" + contentType)
                        .getBytes(StandardCharsets.UTF_8))
                .toString();
    }

}