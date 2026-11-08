package com.ptit.ai_engine_service.service.scoring;

import org.springframework.stereotype.Service;

@Service
public class MatchScoreCalculator {

    private static final double SEMANTIC_WEIGHT = 0.4;
    private static final double SKILL_WEIGHT = 0.6;

    public double calculate(
            double semanticScore,
            double skillScore) {

        double result = SEMANTIC_WEIGHT * semanticScore
                + SKILL_WEIGHT * skillScore;

        return Math.round(result * 100.0) / 100.0;
    }
}