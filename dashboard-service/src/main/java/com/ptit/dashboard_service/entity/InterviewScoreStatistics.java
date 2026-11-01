package com.ptit.dashboard_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interview_score_statistics")
public class InterviewScoreStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jdPosition;

    private Double averageInterviewScore;

    private Integer interviewCount;

    public InterviewScoreStatistics() {
    }

    public InterviewScoreStatistics(
            String jdPosition,
            Double averageInterviewScore,
            Integer interviewCount) {
        this.jdPosition = jdPosition;
        this.averageInterviewScore = averageInterviewScore;
        this.interviewCount = interviewCount;
    }

    public Long getId() {
        return id;
    }

    public String getJdPosition() {
        return jdPosition;
    }

    public void setJdPosition(String jdPosition) {
        this.jdPosition = jdPosition;
    }

    public Double getAverageInterviewScore() {
        return averageInterviewScore;
    }

    public void setAverageInterviewScore(Double averageInterviewScore) {
        this.averageInterviewScore = averageInterviewScore;
    }

    public Integer getInterviewCount() {
        return interviewCount;
    }

    public void setInterviewCount(Integer interviewCount) {
        this.interviewCount = interviewCount;
    }
}