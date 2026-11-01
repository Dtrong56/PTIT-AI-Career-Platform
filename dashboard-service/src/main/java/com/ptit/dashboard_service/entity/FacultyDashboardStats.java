package com.ptit.dashboard_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "faculty_dashboard_stats")
public class FacultyDashboardStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalCVAnalysis;

    public FacultyDashboardStats() {
    }

    public FacultyDashboardStats(Integer totalCVAnalysis) {
        this.totalCVAnalysis = totalCVAnalysis;
    }

    public Long getId() {
        return id;
    }

    public Integer getTotalCVAnalysis() {
        return totalCVAnalysis;
    }

    public void setTotalCVAnalysis(Integer totalCVAnalysis) {
        this.totalCVAnalysis = totalCVAnalysis;
    }
}