
package com.ptit.ai_engine_service.service.scoring;

import org.springframework.stereotype.Service;

@Service
public class MatchScoreCalculator {

    private static final double SEMANTIC_WEIGHT = 0.5;
    private static final double SKILL_WEIGHT = 0.5;

    /**
     * Calculate final match score.
     *
     * @param semanticScore normalized semantic score [0, 1]
     * @param skillScore    skill coverage [0, 1]
     * @param hasSkills     whether JD contains valid skills
     * @return match score [0, 100]
     */
    public double calculate(
            double semanticScore,
            double skillScore,
            boolean hasSkills) {

        validateScore(semanticScore, "semanticScore");
        validateScore(skillScore, "skillScore");

        double result;

        if (!hasSkills) {
            // No skill requirements: evaluate only semantic similarity.
            result = semanticScore * 100.0;
        } else {
            result = (SEMANTIC_WEIGHT * semanticScore
                    + SKILL_WEIGHT * skillScore) * 100.0;
        }

        return round(result);
    }

    private void validateScore(double score, String fieldName) {
        if (Double.isNaN(score)
                || Double.isInfinite(score)
                || score < 0.0
                || score > 1.0) {

            throw new IllegalArgumentException(
                    fieldName + " must be between 0 and 1");
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
