package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.StudentDashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentDashboardStatsRepository
        extends JpaRepository<StudentDashboardStats, UUID> {
}