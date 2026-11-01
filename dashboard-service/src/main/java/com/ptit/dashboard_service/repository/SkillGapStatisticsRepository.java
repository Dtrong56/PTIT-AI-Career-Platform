package com.ptit.dashboard_service.repository;

import com.ptit.dashboard_service.entity.SkillGapStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillGapStatisticsRepository
        extends JpaRepository<SkillGapStatistics, Long> {

    Optional<SkillGapStatistics> findByNormalizedSkill(String normalizedSkill);

    List<SkillGapStatistics> findTop5ByOrderByCountDesc();
}