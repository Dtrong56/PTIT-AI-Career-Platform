package com.ptit.dashboard_service.dto;

import java.util.List;

public record StudentDashboardResponse(
        Integer totalCV,
        Integer totalJD,
        Integer totalAnalysis,
        List<PositionMatchItem> positionMatches) {
    public record PositionMatchItem(
            String jdPosition,
            Integer analysisCount,
            Double averageMatchScore) {
    }
}