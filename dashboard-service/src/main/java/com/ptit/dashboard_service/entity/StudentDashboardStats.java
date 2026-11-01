package com.ptit.dashboard_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_dashboard_stats")
public class StudentDashboardStats {

    @Id
    private Long userId;

    private Integer totalCV;

    private Integer totalJD;

    private Integer totalAnalysis;

    public StudentDashboardStats() {
    }

    public StudentDashboardStats(
            Long userId,
            Integer totalCV,
            Integer totalJD,
            Integer totalAnalysis) {
        this.userId = userId;
        this.totalCV = totalCV;
        this.totalJD = totalJD;
        this.totalAnalysis = totalAnalysis;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTotalCV() {
        return totalCV;
    }

    public void setTotalCV(Integer totalCV) {
        this.totalCV = totalCV;
    }

    public Integer getTotalJD() {
        return totalJD;
    }

    public void setTotalJD(Integer totalJD) {
        this.totalJD = totalJD;
    }

    public Integer getTotalAnalysis() {
        return totalAnalysis;
    }

    public void setTotalAnalysis(Integer totalAnalysis) {
        this.totalAnalysis = totalAnalysis;
    }
}