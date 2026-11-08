package com.ptit.ai_engine_service.service.normalization.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class SkillDictionary {

    private final Map<String, SkillEntry> lookup = new HashMap<>();

    /**
     * Khởi tạo SkillDictionary từ file JSON
     *
     * @param objectMapper ObjectMapper để đọc file JSON
     * @throws Exception nếu có lỗi khi đọc file JSON
     */
    public SkillDictionary(ObjectMapper objectMapper) throws Exception {

        // Tải file skill-dictionary/skills.json từ classpath
        ClassPathResource resource = new ClassPathResource("skill-dictionary/skills.json");

        // Đọc file JSON và ánh xạ vào danh sách các kỹ năng
        try (InputStream inputStream = resource.getInputStream()) {

            SkillDictionaryFile dictionary = objectMapper.readValue(
                    inputStream,
                    SkillDictionaryFile.class);

            for (SkillEntry skill : dictionary.skills()) {

                register(skill.name(), skill);

                if (skill.aliases() != null) {
                    for (String alias : skill.aliases()) {
                        register(alias, skill);
                    }
                }
            }
        }
    }

    /**
     * Đăng ký một kỹ năng vào từ điển
     *
     * @param value giá trị (tên hoặc bí danh) của kỹ năng
     * @param skill đối tượng SkillEntry đại diện cho kỹ năng
     * @throws IllegalStateException nếu có trùng lặp tên hoặc bí danh
     */
    private void register(String value, SkillEntry skill) {

        String key = normalizeKey(value);

        SkillEntry existing = lookup.putIfAbsent(key, skill);

        if (existing != null
                && !existing.id().equals(skill.id())) {
            throw new IllegalStateException(
                    "Duplicate skill name or alias: " + value);
        }
    }

    /**
     * Tìm kiếm một kỹ năng trong từ điển dựa trên tên hoặc bí danh
     *
     * @param name tên hoặc bí danh của kỹ năng
     * @return đối tượng SkillEntry nếu tìm thấy, null nếu không tìm thấy
     */
    public SkillEntry find(String name) {
        return lookup.get(normalizeKey(name));
    }

    /**
     * Chuẩn hóa giá trị đầu vào để làm khóa tìm kiếm
     *
     * @param value giá trị đầu vào (tên hoặc bí danh)
     * @return giá trị đã chuẩn hóa
     */
    private String normalizeKey(String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                value.trim().replaceAll("\\s+", " "),
                Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Record đại diện cho file từ điển kỹ năng
     */
    public record SkillDictionaryFile(List<SkillEntry> skills) {
    }

    /**
     * Record đại diện cho một kỹ năng
     */
    public record SkillEntry(
            String id,
            String name,
            List<String> aliases,
            String category) {
    }
}