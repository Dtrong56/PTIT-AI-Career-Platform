package com.ptit.dashboard_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "student_dashboard_stats")
public class StudentDashboardStats {

    @Id
    private UUID studentId;

    private Integer totalCV;

    private Integer totalJD;

    private Integer totalAnalysis;

    public StudentDashboardStats() {
    }

    public StudentDashboardStats(
            UUID studentId,
            Integer totalCV,
            Integer totalJD,
            Integer totalAnalysis) {
        this.studentId = studentId;
        this.totalCV = totalCV;
        this.totalJD = totalJD;
        this.totalAnalysis = totalAnalysis;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
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