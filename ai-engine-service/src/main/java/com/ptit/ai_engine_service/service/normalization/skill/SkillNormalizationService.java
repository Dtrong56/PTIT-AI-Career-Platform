package com.ptit.ai_engine_service.service.normalization.skill;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class SkillNormalizationService {

    private final SkillDictionary skillDictionary;

    // Constructor
    public SkillNormalizationService(
            SkillDictionary skillDictionary) {
        this.skillDictionary = skillDictionary;
    }

    /**
     * Normalize the extracted skill name and category.
     *
     * @param extractedName     The extracted skill name.
     * @param extractedCategory The extracted skill category.
     * @return A NormalizedSkill object containing the normalized name and category.
     */
    public NormalizedSkill normalize(
            String extractedName,
            String extractedCategory) {

        if (extractedName == null || extractedName.isBlank()) {
            return null;
        }

        String cleanedName = extractedName
                .trim()
                .replaceAll("\\s+", " ");

        SkillDictionary.SkillEntry entry = skillDictionary.find(cleanedName);

        if (entry != null) {
            return new NormalizedSkill(
                    entry.name(),
                    entry.category());
        }

        String category = extractedCategory == null
                || extractedCategory.isBlank()
                        ? "OTHER"
                        : extractedCategory.trim().toUpperCase(Locale.ROOT);

        return new NormalizedSkill(cleanedName, category);
    }

    // Record to hold the normalized skill name and category.
    public record NormalizedSkill(
            String name,
            String category) {
    }
}