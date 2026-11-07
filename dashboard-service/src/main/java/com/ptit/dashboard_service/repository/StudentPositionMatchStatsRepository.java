package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.StudentPositionMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentPositionMatchStatsRepository
                extends JpaRepository<StudentPositionMatchStats, Long> {

        List<StudentPositionMatchStats> findByStudentId(UUID studentId);

        Optional<StudentPositionMatchStats> findByStudentIdAndJdPosition(
                        UUID studentId,
                        String jdPosition);
}