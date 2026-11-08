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

        if (jdId == null) {
            throw new IllegalArgumentException("jdId must not be null.");
        }

        if (structuredContent == null || structuredContent.isBlank()) {
            throw new IllegalArgumentException(
                    "JD structured content must not be empty.");
        }

        JsonNode jd = objectMapper.readTree(structuredContent);
        List<Document> documents = new ArrayList<>();

        // 1. Job title
        addJDDocument(
                documents,
                jdId,
                "OVERVIEW",
                buildJDOverviewContent(jd));

        // 2. Responsibilities
        addJDDocument(
                documents,
                jdId,
                "RESPONSIBILITIES",
                buildListContent(
                        "Job responsibilities",
                        jd.path("responsibilities")));

        // 3. Qualifications
        addJDDocument(
                documents,
                jdId,
                "QUALIFICATIONS",
                buildListContent(
                        "Candidate qualifications",
                        jd.path("qualifications")));

        // 4. Required and preferred skills
        addJDDocument(
                documents,
                jdId,
                "SKILLS",
                buildSkillsContent(jd.path("skills")));

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
    private String buildJDOverviewContent(JsonNode jd) {
        StringBuilder content = new StringBuilder();

        appendField(content, "Job title", jd.path("jobTitle"));

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

        if (items == null || !items.isArray() || items.isEmpty()) {
            return "";
        }

        StringBuilder content = new StringBuilder();

        for (JsonNode item : items) {
            if (item.isTextual() && !item.asText().isBlank()) {
                if (content.isEmpty()) {
                    content.append(title).append(":\n");
                }

                content.append("- ")
                        .append(item.asText().trim())
                        .append("\n");
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

        if (skills == null || !skills.isArray() || skills.isEmpty()) {
            return "";
        }

        StringBuilder content = new StringBuilder();

        for (JsonNode skill : skills) {

            String name = skill.path("name").asText("");
            String type = skill.path("type").asText("");
            String category = skill.path("category").asText("");

            if (name.isBlank()) {
                continue;
            }

            if (content.isEmpty()) {
                content.append("Job skills:\n");
            }

            content.append("- ")
                    .append(name.trim())
                    .append(" | Type: ")
                    .append(type)
                    .append(" | Category: ")
                    .append(category)
                    .append("\n");
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
    private void addJDDocument(
            List<Document> documents,
            UUID jdId,
            String contentType,
            String content) {

        if (content == null || content.isBlank()) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("documentType", "JD");
        metadata.put("jdId", jdId.toString());
        metadata.put("contentType", contentType);

        // JD
        String documentId = createDocumentId(jdId, "JD", contentType);

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
     * Tạo nội dung tóm tắt từ CV.
     */
    private String buildSummaryContent(JsonNode cv) {
        StringBuilder content = new StringBuilder();

        appendField(content, "Professional summary", cv.path("summary"));

        return content.toString().trim();
    }

    /**
     * Tạo nội dung giáo dục từ CV.
     * Chỉ tạo document khi có thông tin thực tế.
     */
    private String buildEducationContent(JsonNode education) {
        if (education == null || !education.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode item : education) {
            if (!item.isObject()) {
                continue;
            }

            StringBuilder itemContent = new StringBuilder();

            appendField(itemContent, "School", item.path("school"));
            appendField(itemContent, "Degree", item.path("degree"));

            // Major là thông tin quan trọng trong nghiên cứu CV-JD.
            appendField(itemContent, "Major", item.path("major"));

            appendField(itemContent, "Start date", item.path("startDate"));
            appendField(itemContent, "End date", item.path("endDate"));
            appendField(itemContent, "GPA", item.path("gpa"));

            appendTextArray(
                    itemContent,
                    "Details",
                    item.path("details"));

            if (!itemContent.isEmpty()) {
                body.append(itemContent).append("\n");
            }
        }

        return buildSectionContent("Education", body);
    }

    /**
     * Tạo nội dung kinh nghiệm làm việc từ CV.
     * Giữ riêng từng kinh nghiệm và bằng chứng kỹ thuật.
     */
    private String buildExperienceContent(JsonNode experience) {
        if (experience == null || !experience.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode item : experience) {
            if (!item.isObject()) {
                continue;
            }

            StringBuilder itemContent = new StringBuilder();

            appendField(itemContent, "Company", item.path("company"));
            appendField(itemContent, "Position", item.path("position"));
            appendField(
                    itemContent,
                    "Employment type",
                    item.path("employmentType"));
            appendField(itemContent, "Start date", item.path("startDate"));
            appendField(itemContent, "End date", item.path("endDate"));

            // Giữ nguyên mô tả nhiệm vụ, trách nhiệm và thành tựu.
            appendTextArray(
                    itemContent,
                    "Responsibilities and achievements",
                    item.path("description"));

            // Giữ danh sách công nghệ riêng với phần mô tả.
            appendTextArray(
                    itemContent,
                    "Technologies",
                    item.path("technologies"));

            if (!itemContent.isEmpty()) {
                body.append(itemContent).append("\n");
            }
        }

        return buildSectionContent("Professional experience", body);
    }

    /**
     * Tạo nội dung dự án từ CV.
     * Giữ riêng mô tả và công nghệ của từng dự án.
     */
    private String buildProjectsContent(JsonNode projects) {
        if (projects == null || !projects.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode item : projects) {
            if (!item.isObject()) {
                continue;
            }

            StringBuilder itemContent = new StringBuilder();

            appendField(itemContent, "Project name", item.path("name"));
            appendField(itemContent, "Role", item.path("role"));
            appendField(itemContent, "Start date", item.path("startDate"));
            appendField(itemContent, "End date", item.path("endDate"));
            appendField(itemContent, "Project URL", item.path("url"));

            appendTextArray(
                    itemContent,
                    "Project description",
                    item.path("description"));

            appendTextArray(
                    itemContent,
                    "Technologies",
                    item.path("technologies"));

            if (!itemContent.isEmpty()) {
                body.append(itemContent).append("\n");
            }
        }

        return buildSectionContent("Projects", body);
    }

    /**
     * Tạo nội dung kỹ năng từ CV.
     */
    private String buildCVSkillsContent(JsonNode skills) {
        if (skills == null || !skills.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode skill : skills) {
            if (!skill.isObject()) {
                continue;
            }

            String name = skill.path("name").asText("").trim();

            if (name.isBlank()) {
                continue;
            }

            StringBuilder skillContent = new StringBuilder("- ")
                    .append(name);

            String category = skill.path("category").asText("").trim();
            String level = skill.path("level").asText("").trim();

            if (!category.isBlank()) {
                skillContent.append(" | Category: ")
                        .append(category);
            }

            if (!level.isBlank()) {
                skillContent.append(" | Level: ")
                        .append(level);
            }

            body.append(skillContent).append("\n");
        }

        return buildSectionContent("Candidate skills", body);
    }

    /**
     * Tạo nội dung chứng chỉ từ CV.
     */
    private String buildCertificationsContent(JsonNode certifications) {
        if (certifications == null || !certifications.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode item : certifications) {
            if (!item.isObject()) {
                continue;
            }

            StringBuilder itemContent = new StringBuilder();

            appendField(itemContent, "Certification", item.path("name"));
            appendField(itemContent, "Issuer", item.path("issuer"));
            appendField(itemContent, "Date", item.path("date"));

            if (!itemContent.isEmpty()) {
                body.append(itemContent).append("\n");
            }
        }

        return buildSectionContent("Certifications", body);
    }

    /**
     * Tạo nội dung ngôn ngữ từ CV.
     */
    private String buildLanguagesContent(JsonNode languages) {
        if (languages == null || !languages.isArray()) {
            return "";
        }

        StringBuilder body = new StringBuilder();

        for (JsonNode item : languages) {
            if (!item.isObject()) {
                continue;
            }

            StringBuilder itemContent = new StringBuilder();

            appendField(itemContent, "Language", item.path("name"));
            appendField(
                    itemContent,
                    "Proficiency",
                    item.path("proficiency"));

            if (!itemContent.isEmpty()) {
                body.append(itemContent).append("\n");
            }
        }

        return buildSectionContent("Languages", body);
    }

    /**
     * Chỉ thêm tiêu đề section khi section có nội dung thực tế.
     */
    private String buildSectionContent(
            String title,
            StringBuilder body) {
        if (body == null || body.isEmpty()) {
            return "";
        }

        return title + ":\n" + body.toString().trim();
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
        if (content == null
                || values == null
                || !values.isArray()) {
            return;
        }

        StringBuilder items = new StringBuilder();

        for (JsonNode value : values) {
            if (value == null || !value.isTextual()) {
                continue;
            }

            String text = value.asText().trim();

            if (text.isBlank()) {
                continue;
            }

            items.append("- ")
                    .append(text)
                    .append("\n");
        }

        // Chỉ thêm label nếu có ít nhất một phần tử hợp lệ.
        if (!items.isEmpty()) {
            content.append(label)
                    .append(":\n")
                    .append(items);
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

        String documentId = createDocumentId(cvId, "CV", contentType);

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
    private String createDocumentId(
            UUID entityId,
            String documentType,
            String contentType) {

        return UUID.nameUUIDFromBytes(
                (documentType + "_" + entityId + "_" + contentType)
                        .getBytes(StandardCharsets.UTF_8))
                .toString();
    }

}