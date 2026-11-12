package com.ptit.ai_engine_service.service.normalization.skill;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class SkillNormalizationService {

    private static final Set<String> VALID_CATEGORIES = Set.of(
            "PROGRAMMING_LANGUAGE", "FRAMEWORK", "DATABASE", "TOOL",
            "PLATFORM", "RUNTIME", "SOFT_SKILL", "OTHER");

    private final SkillDictionary skillDictionary;

    public SkillNormalizationService(SkillDictionary skillDictionary) {
        this.skillDictionary = skillDictionary;
    }

    /**
     * Chỉ trả về kết quả khi chuỗi có trong từ điển (tên chính hoặc alias).
     */
    public Optional<NormalizedSkill> findKnown(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        SkillDictionary.SkillEntry entry = skillDictionary.find(cleanName(name));

        return entry == null
                ? Optional.empty()
                : Optional.of(new NormalizedSkill(entry.name(), entry.category()));
    }

    public NormalizedSkill normalize(String extractedName, String extractedCategory) {
        if (extractedName == null || extractedName.isBlank()) {
            return null;
        }

        String cleanedName = cleanName(extractedName);

        Optional<NormalizedSkill> known = findKnown(cleanedName);
        if (known.isPresent()) {
            return known.get();
        }

        return new NormalizedSkill(cleanedName, sanitizeCategory(extractedCategory));
    }

    private String cleanName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private String sanitizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "OTHER";
        }
        String upper = category.trim().toUpperCase(Locale.ROOT);
        return VALID_CATEGORIES.contains(upper) ? upper : "OTHER";
    }

    public record NormalizedSkill(String name, String category) {
    }
}