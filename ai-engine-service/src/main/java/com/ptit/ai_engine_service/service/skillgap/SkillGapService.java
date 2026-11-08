package com.ptit.ai_engine_service.service.skillgap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.ai_engine_service.dto.enums.MatchStatus;
import com.ptit.ai_engine_service.dto.enums.SkillType;
import com.ptit.ai_engine_service.dto.response.SkillGapResult;
import com.ptit.ai_engine_service.service.scoring.SimilarityService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SkillGapService {

    private static final double MATCH_THRESHOLD = 0.82;
    private static final double PARTIAL_THRESHOLD = 0.65;

    private static final double REQUIRED_WEIGHT = 2.0;
    private static final double PREFERRED_WEIGHT = 1.0;

    private final ObjectMapper objectMapper;
    private final SimilarityService similarityService;

    public SkillGapService(
            ObjectMapper objectMapper,
            SimilarityService similarityService) {

        this.objectMapper = objectMapper;
        this.similarityService = similarityService;
    }

    public Result analyze(
            String structuredCv,
            String structuredJd) throws Exception {

        JsonNode cv = objectMapper.readTree(structuredCv);
        JsonNode jd = objectMapper.readTree(structuredJd);

        Map<String, SkillCandidate> cvSkills = extractCVSkills(cv);
        JsonNode jdSkills = jd.path("skills");

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

        double skillScore = totalWeight == 0
                ? 0.0
                : round(totalWeightedScore / totalWeight * 100);

        return new Result(results, skillScore);
    }

    private SkillGapResult matchSkill(
            String jdSkill,
            SkillType type,
            Map<String, SkillCandidate> cvSkills) {

        String normalizedJD = normalizeSkill(jdSkill);

        // 1. Exact matching
        SkillCandidate exactMatch = cvSkills.get(normalizedJD);

        if (exactMatch != null) {
            return new SkillGapResult(
                    jdSkill,
                    type,
                    MatchStatus.MATCHED,
                    1.0,
                    exactMatch.evidence());
        }

        // 2. Semantic matching
        SkillCandidate bestCandidate = null;
        double bestSimilarity = 0.0;

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
                // Bỏ qua candidate không thể tính embedding.
            }
        }

        if (bestCandidate != null
                && bestSimilarity >= MATCH_THRESHOLD) {

            return new SkillGapResult(
                    jdSkill,
                    type,
                    MatchStatus.MATCHED,
                    1.0,
                    "Semantically matched with "
                            + bestCandidate.name()
                            + " (" + bestCandidate.evidence() + ")");
        }

        if (bestCandidate != null
                && bestSimilarity >= PARTIAL_THRESHOLD) {

            return new SkillGapResult(
                    jdSkill,
                    type,
                    MatchStatus.PARTIAL,
                    0.5,
                    "Partially matched with "
                            + bestCandidate.name()
                            + " (" + bestCandidate.evidence() + ")");
        }

        return new SkillGapResult(
                jdSkill,
                type,
                MatchStatus.MISSING,
                0.0,
                null);
    }

    private Map<String, SkillCandidate> extractCVSkills(JsonNode cv) {

        Map<String, SkillCandidate> skills = new LinkedHashMap<>();

        // 1. Explicit CV skills
        JsonNode skillNodes = cv.path("skills");

        if (skillNodes.isArray()) {
            for (JsonNode skill : skillNodes) {

                String name = skill.path("name").asText("").trim();

                if (!name.isBlank()) {
                    addSkill(
                            skills,
                            name,
                            "CV skills");
                }
            }
        }

        // 2. Experience technologies
        extractTechnologies(
                skills,
                cv.path("experience"),
                "technologies",
                "Work experience");

        // 3. Project technologies
        extractTechnologies(
                skills,
                cv.path("projects"),
                "technologies",
                "Projects");

        return skills;
    }

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
                        addSkill(skills, name, source);
                    }
                }
            }
        }
    }

    private void addSkill(
            Map<String, SkillCandidate> skills,
            String name,
            String evidence) {

        skills.putIfAbsent(
                normalizeSkill(name),
                new SkillCandidate(name, evidence));
    }

    private String normalizeSkill(String value) {

        String normalized = value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        return switch (normalized) {
            case "springboot", "spring boot framework" ->
                "spring boot";

            case "reactjs", "react js" ->
                "react";

            case "nodejs", "node js" ->
                "node.js";

            case "postgres sql", "postgresql database" ->
                "postgresql";

            case "vuejs", "vue js" ->
                "vue.js";

            default -> normalized;
        };
    }

    private SkillType parseSkillType(String value) {

        try {
            return SkillType.valueOf(
                    value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            return SkillType.REQUIRED;
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record SkillCandidate(
            String name,
            String evidence) {
    }

    public record Result(
            List<SkillGapResult> skillGaps,
            double skillScore) {
    }
}