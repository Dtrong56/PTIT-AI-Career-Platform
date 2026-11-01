package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.entity.Analysis;
import com.ptit.core_platform_service.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;

    public Analysis getById(UUID analysisId) {
        return analysisRepository.findById(analysisId)
                .orElseThrow(() -> new RuntimeException(
                        "Analysis not found: " + analysisId));
    }

    public List<Analysis> getByCvId(UUID cvId) {
        return analysisRepository.findByCvCvId(cvId);
    }

    public List<Analysis> getByJdId(UUID jdId) {
        return analysisRepository.findByJdJdId(jdId);
    }
}