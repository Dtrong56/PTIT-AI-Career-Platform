package com.ptit.dashboard_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_position_match_stats")
public class StudentPositionMatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String jdPosition;

    private Integer analysisCount;

    private Double averageMatchScore;

    public StudentPositionMatchStats() {
    }

    public StudentPositionMatchStats(
            Long userId,
            String jdPosition,
            Integer analysisCount,
            Double averageMatchScore) {
        this.userId = userId;
        this.jdPosition = jdPosition;
        this.analysisCount = analysisCount;
        this.averageMatchScore = averageMatchScore;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJdPosition() {
        return jdPosition;
    }

    public void setJdPosition(String jdPosition) {
        this.jdPosition = jdPosition;
    }

    public Integer getAnalysisCount() {
        return analysisCount;
    }

    public void setAnalysisCount(Integer analysisCount) {
        this.analysisCount = analysisCount;
    }

    public Double getAverageMatchScore() {
        return averageMatchScore;
    }

    public void setAverageMatchScore(Double averageMatchScore) {
        this.averageMatchScore = averageMatchScore;
    }
}