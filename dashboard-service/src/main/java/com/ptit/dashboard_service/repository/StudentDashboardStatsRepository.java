package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.StudentDashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentDashboardStatsRepository
        extends JpaRepository<StudentDashboardStats, Long> {

    Optional<StudentDashboardStats> findByUserId(Long userId);
}