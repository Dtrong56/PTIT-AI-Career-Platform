package com.ptit.dashboard_service.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "student_position_match_stats")
public class StudentPositionMatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID studentId;

    private String jdPosition;

    private Integer analysisCount;

    private Double averageMatchScore;

    public StudentPositionMatchStats() {
    }

    public StudentPositionMatchStats(
            UUID studentId,
            String jdPosition,
            Integer analysisCount,
            Double averageMatchScore) {
        this.studentId = studentId;
        this.jdPosition = jdPosition;
        this.analysisCount = analysisCount;
        this.averageMatchScore = averageMatchScore;
    }

    public Long getId() {
        return id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
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