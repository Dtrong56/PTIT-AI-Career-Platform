package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.SkillGap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillGapRepository extends JpaRepository<SkillGap, UUID> {

    List<SkillGap> findByAnalysisAnalysisId(UUID analysisId);
}