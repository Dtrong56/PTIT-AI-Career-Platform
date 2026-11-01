package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.StudentPositionMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentPositionMatchStatsRepository
        extends JpaRepository<StudentPositionMatchStats, Long> {

    List<StudentPositionMatchStats> findByUserId(Long userId);

    Optional<StudentPositionMatchStats> findByUserIdAndJdPosition(
            Long userId,
            String jdPosition);
}