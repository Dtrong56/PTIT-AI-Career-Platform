package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.FacultyDashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyDashboardStatsRepository
                extends JpaRepository<FacultyDashboardStats, Long> {

        Optional<FacultyDashboardStats> findFirstByOrderByIdAsc();
}