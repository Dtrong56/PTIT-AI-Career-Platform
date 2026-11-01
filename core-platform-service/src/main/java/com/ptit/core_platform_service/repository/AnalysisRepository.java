package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    List<Analysis> findByCvCvId(UUID cvId);

    List<Analysis> findByJdJdId(UUID jdId);
}