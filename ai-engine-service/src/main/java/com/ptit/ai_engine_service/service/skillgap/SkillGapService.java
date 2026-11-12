package com.ptit.ai_engine_service.service.skillgap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.ai_engine_service.dto.enums.MatchStatus;
import com.ptit.ai_engine_service.dto.enums.SkillType;
import com.ptit.ai_engine_service.dto.response.SkillGapResult;
import com.ptit.ai_engine_service.service.normalization.skill.SkillNormalizationService;
import com.ptit.ai_engine_service.service.similarity.SimilarityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SkillGapService {

    private static final double SEMANTIC_MATCH_THRESHOLD = 0.82;

    private static final double REQUIRED_WEIGHT = 1.0;
    private static final double PREFERRED_WEIGHT = 0.5;

    private final ObjectMapper objectMapper;
    private final SimilarityService similarityService;
    private final SkillNormalizationService skillNormalizationService;
    private final SkillHierarchy skillHierarchy;

    // Constructor
    public SkillGapService(
            ObjectMapper objectMapper,
            SimilarityService similarityService,
            SkillNormalizationService skillNormalizationService,
            SkillHierarchy skillHierarchy) {
        this.objectMapper = objectMapper;
        this.similarityService = similarityService;
        this.skillNormalizationService = skillNormalizationService;
        this.skillHierarchy = skillHierarchy;
    }

    /**
     * Analyze the skill gap between a structured CV and a structured JD.
     *
     * @param structuredCv The structured CV in JSON format.
     * @param structuredJd The structured JD in JSON format.
     * @return A Result object containing the skill gap analysis results and the
     *         overall skill score.
     * @throws Exception If there is an error during the analysis process.
     */
    public Result analyze(
            String structuredCv,
            String structuredJd) throws Exception {

        JsonNode cv = objectMapper.readTree(structuredCv);
        JsonNode jd = objectMapper.readTree(structuredJd);

        JsonNode jdSkills = jd.path("skills");
        Map<String, SkillCandidate> cvSkills = extractCVSkills(cv, jdSkills);

        List<SkillGapResult> results = new ArrayList<>();

        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        if (!jdSkills.isArray()) {
            return new Result(List.of(), 0.0);
        }

        for (JsonNode jdSkill : jdSkills) {

            String skillName = jdSkill.path("name").asText("").trim();

            if (skillName.isBlank()) {
                continue;
            }

            SkillType type = parseSkillType(
                    jdSkill.path("type").asText("REQUIRED"));

            double weight = type == SkillType.REQUIRED
                    ? REQUIRED_WEIGHT
                    : PREFERRED_WEIGHT;

            SkillGapResult gap = matchSkill(
                    skillName,
                    type,
                    cvSkills);

            results.add(gap);

            totalWeightedScore += gap.score() * weight;
            totalWeight += weight;
        }

        double skillScore = totalWeight == 0.0
                ? 0.0
                : totalWeightedScore / totalWeight;

        return new Result(
                results,
                skillScore);
    }

    /**
     * Match a single JD skill against the CV skills.
     *
     * @param jdSkill  The JD skill to match.
     * @param type     The type of the JD skill (REQUIRED or PREFERRED).
     * @param cvSkills The map of CV skills to match against.
     * @return A SkillGapResult representing the match result for the JD skill.
     */
    private SkillGapResult matchSkill(
            String jdSkill,
            SkillType type,
            Map<String, SkillCandidate> cvSkills) {

        String normalizedJD = normalizeSkill(jdSkill);

        // 1. Exact matching
        SkillCandidate exactMatch = cvSkills.get(normalizedJD);

        if (exactMatch != null) {
            if (exactMatch.hasEvidence()) {
                return matched(
                        jdSkill,
                        type,
                        1.0,
                        exactMatch.evidenceSummary());
            }

            return unclear(jdSkill, type);
        }

        // 2. Hierarchical matching
        for (SkillCandidate candidate : cvSkills.values()) {

            if (skillHierarchy.isPartialMatch(
                    normalizedJD,
                    candidate.normalizedName())) {
                if (candidate.hasEvidence()) {
                    return new SkillGapResult(
                            jdSkill,
                            type,
                            MatchStatus.PARTIAL,
                            0.5,
                            "CV demonstrates related skill: "
                                    + candidate.name()
                                    + ". Evidence: "
                                    + candidate.evidenceSummary());
                }

                return unclear(jdSkill, type);
            }
        }

        // 3. Semantic matching
        SkillCandidate bestCandidate = null;
        double bestSimilarity = -1.0;

        for (SkillCandidate candidate : cvSkills.values()) {

            try {
                double similarity = similarityService.calculateCosineSimilarity(
                        jdSkill,
                        candidate.name());

                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestCandidate = candidate;
                }

            } catch (Exception exception) {
                throw new IllegalStateException(
                        "Cannot calculate skill similarity between "
                                + jdSkill + " and " + candidate.name(),
                        exception);
            }
        }

        if (bestCandidate != null
                && bestSimilarity >= SEMANTIC_MATCH_THRESHOLD) {

            if (bestCandidate.hasEvidence()) {
                return matched(
                        jdSkill,
                        type,
                        1.0,
                        "Semantically matched with "
                                + bestCandidate.name()
                                + ". Similarity: "
                                + round(bestSimilarity)
                                + ". Evidence: "
                                + bestCandidate.evidenceSummary());
            }

            return unclear(jdSkill, type);
        }

        // 4. No matching candidate
        return new SkillGapResult(
                jdSkill,
                type,
                MatchStatus.MISSING,
                0.0,
                null);
    }

    /**
     * Extract skills from the CV, including explicit skills, experience
     * technologies,
     * project technologies, and evidence from descriptions.
     *
     * @param cv       The structured CV in JSON format.
     * @param jdSkills The structured JD skills in JSON format.
     * @return A map of skill names to SkillCandidate objects representing the
     *         extracted skills.
     */
    private Map<String, SkillCandidate> extractCVSkills(
            JsonNode cv,
            JsonNode jdSkills) {

        Map<String, SkillCandidate> skills = new LinkedHashMap<>();

        // 1. Explicit skills: declared skills are not evidence by themselves.
        JsonNode skillNodes = cv.path("skills");

        if (skillNodes.isArray()) {
            for (JsonNode skill : skillNodes) {

                String name = skill.path("name").asText("").trim();

                if (!name.isBlank()) {
                    addSkill(
                            skills,
                            name,
                            false,
                            "CV skills");
                }
            }
        }

        // 2. Experience technologies are evidence.
        extractTechnologies(
                skills,
                cv.path("experience"),
                "technologies",
                "Work experience");

        // 3. Project technologies are evidence.
        extractTechnologies(
                skills,
                cv.path("projects"),
                "technologies",
                "Projects");

        // 4. Descriptions can provide evidence.
        extractDescriptionEvidence(
                skills,
                cv.path("experience"),
                "Work experience");

        extractDescriptionEvidence(
                skills,
                cv.path("projects"),
                "Projects");

        // 5. Skills mentioned directly in experience/project descriptions.
        // Only add them when the JD skill phrase is actually found.
        if (jdSkills != null && jdSkills.isArray()) {
            for (JsonNode jdSkill : jdSkills) {
                String name = jdSkill.path("name").asText("").trim();

                if (name.isBlank()) {
                    continue;
                }

                markJDDescriptionEvidence(
                        skills,
                        name,
                        cv.path("experience"),
                        "Work experience");

                markJDDescriptionEvidence(
                        skills,
                        name,
                        cv.path("projects"),
                        "Projects");
            }
        }

        return skills;
    }

    /**
     * Extract technologies from a given field in the CV items and add them to the
     * skills map.
     *
     * @param skills The map of skill names to SkillCandidate objects.
     * @param items  The JSON array of CV items (experience or projects).
     * @param field  The field name containing the technologies.
     * @param source The source description for evidence.
     */
    private void extractTechnologies(
            Map<String, SkillCandidate> skills,
            JsonNode items,
            String field,
            String source) {

        if (!items.isArray()) {
            return;
        }

        for (JsonNode item : items) {

            JsonNode technologies = item.path(field);

            if (!technologies.isArray()) {
                continue;
            }

            for (JsonNode technology : technologies) {

                if (technology.isTextual()) {

                    String name = technology.asText().trim();

                    if (!name.isBlank()) {
                        addSkill(
                                skills,
                                name,
                                true,
                                source);
                    }
                }
            }
        }
    }

    /**
     * Extract skills from the descriptions of CV items and add them to the skills
     * map.
     *
     * @param skills The map of skill names to SkillCandidate objects.
     * @param items  The JSON array of CV items (experience or projects).
     * @param source The source description for evidence.
     */
    private void extractDescriptionEvidence(
            Map<String, SkillCandidate> skills,
            JsonNode items,
            String source) {

        if (!items.isArray()) {
            return;
        }

        for (JsonNode item : items) {

            JsonNode description = item.path("description");

            if (description.isArray()) {
                for (JsonNode sentence : description) {
                    if (sentence.isTextual()) {
                        markDescriptionEvidence(
                                skills,
                                sentence.asText(),
                                source);
                    }
                }
            } else if (description.isTextual()) {
                markDescriptionEvidence(
                        skills,
                        description.asText(),
                        source);
            }
        }
    }

    /**
     * HELPER METHODS
     */
    private void markDescriptionEvidence(
            Map<String, SkillCandidate> skills,
            String description,
            String source) {

        if (description == null || description.isBlank()) {
            return;
        }

        for (SkillCandidate candidate : skills.values()) {

            if (containsSkillPhrase(
                    description,
                    candidate.name())) {
                candidate.addEvidence(source + ": " + description.trim());
            }
        }
    }

    private boolean containsSkillPhrase(
            String text,
            String skill) {

        String normalizedText = text.toLowerCase(Locale.ROOT);
        String normalizedSkill = skill.toLowerCase(Locale.ROOT);

        String regex = "(?<![\\p{L}\\p{N}])"
                + java.util.regex.Pattern.quote(normalizedSkill)
                + "(?![\\p{L}\\p{N}])";

        return java.util.regex.Pattern
                .compile(regex)
                .matcher(normalizedText)
                .find();
    }

    private void addSkill(
            Map<String, SkillCandidate> skills,
            String name,
            boolean hasEvidence,
            String source) {

        String normalized = normalizeSkill(name);

        SkillCandidate candidate = skills.computeIfAbsent(
                normalized,
                key -> new SkillCandidate(name, normalized));

        if (hasEvidence) {
            candidate.addEvidence(source);
        }
    }

    private String normalizeSkill(String name) {

        return skillNormalizationService
                .normalize(name, null)
                .name()
                .toLowerCase(Locale.ROOT);
    }

    private SkillType parseSkillType(String value) {

        try {
            return SkillType.valueOf(
                    value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            return SkillType.REQUIRED;
        }
    }

    private SkillGapResult matched(
            String skillName,
            SkillType type,
            double score,
            String evidence) {
        return new SkillGapResult(
                skillName,
                type,
                MatchStatus.MATCHED,
                score,
                evidence);
    }

    private SkillGapResult unclear(
            String skillName,
            SkillType type) {
        return new SkillGapResult(
                skillName,
                type,
                MatchStatus.UNCLEAR,
                0.0,
                "CV lists this skill, but no supporting evidence "
                        + "was found in work experience or projects.");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class SkillCandidate {

        private final String name;
        private final String normalizedName;
        private final List<String> evidence = new ArrayList<>();

        private SkillCandidate(
                String name,
                String normalizedName) {
            this.name = name;
            this.normalizedName = normalizedName;
        }

        private void addEvidence(String source) {
            if (!evidence.contains(source)) {
                evidence.add(source);
            }
        }

        private boolean hasEvidence() {
            return !evidence.isEmpty();
        }

        private String name() {
            return name;
        }

        private String normalizedName() {
            return normalizedName;
        }

        private String evidenceSummary() {
            return String.join("; ", evidence);
        }
    }

    private void markJDDescriptionEvidence(
            Map<String, SkillCandidate> skills,
            String jdSkill,
            JsonNode items,
            String source) {
        if (items == null || !items.isArray()) {
            return;
        }

        for (JsonNode item : items) {
            JsonNode description = item.path("description");

            if (description.isArray()) {
                for (JsonNode sentence : description) {
                    if (sentence.isTextual()
                            && containsSkillPhrase(sentence.asText(), jdSkill)) {
                        addSkill(skills, jdSkill, true, source);
                    }
                }
            } else if (description.isTextual()
                    && containsSkillPhrase(description.asText(), jdSkill)) {
                addSkill(skills, jdSkill, true, source);
            }
        }
    }

    // record class to hold the result of skill gap analysis
    public record Result(
            List<SkillGapResult> skillGaps,
            double skillScore) {
    }
}
