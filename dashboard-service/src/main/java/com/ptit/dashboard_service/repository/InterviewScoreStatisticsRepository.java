package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.InterviewScoreStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewScoreStatisticsRepository
        extends JpaRepository<InterviewScoreStatistics, Long> {

    Optional<InterviewScoreStatistics> findByJdPosition(String jdPosition);
}