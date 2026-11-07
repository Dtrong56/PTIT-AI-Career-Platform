package com.ptit.dashboard_service.dto;

import java.util.List;

public record FacultyDashboardResponse(
        Integer totalCVAnalysis,
        List<SkillGapItem> topSkillGaps,
        List<InterviewScoreItem> interviewScores) {
    public record SkillGapItem(
            String skillName,
            Integer count) {
    }

    public record InterviewScoreItem(
            String jdPosition,
            Double averageInterviewScore,
            Integer interviewCount) {
    }
}