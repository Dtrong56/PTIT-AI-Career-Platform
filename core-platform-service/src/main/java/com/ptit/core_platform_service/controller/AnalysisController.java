package com.ptit.core_platform_service.controller;

import com.ptit.core_platform_service.dto.analysis.AnalysisResponse;
import com.ptit.core_platform_service.dto.analysis.CreateAnalysisRequest;
import com.ptit.core_platform_service.entity.enums.AnalysisStatus;
import com.ptit.core_platform_service.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * POST /api/core/analyses
     *
     * Flow:
     * Frontend
     * -> API Gateway
     * -> AnalysisController
     * -> AnalysisService
     * -> Validate CV + JD
     * -> Create Analysis
     * -> CV_ANALYSIS_REQUEST -> RabbitMQ -> AI Engine
     */
    @PostMapping
    public AnalysisResponse createAnalysis(
            @Valid @RequestBody CreateAnalysisRequest request) {

        return analysisService.createAnalysis(request);
    }

    /**
     * GET /api/core/analyses?studentId={studentId}
     *
     * Lấy lịch sử phân tích của sinh viên.
     */
    @GetMapping
    public List<AnalysisResponse> getAnalyses(
            @RequestParam UUID studentId) {

        return analysisService.getAnalyses(studentId);
    }

    /**
     * GET /api/core/analyses/{analysisId}
     *
     * Lấy kết quả phân tích:
     * - Match Score
     * - Semantic Score
     * - Skill Score
     * - Skill Gap
     */
    @GetMapping("/{analysisId}")
    public AnalysisResponse getAnalysis(
            @PathVariable UUID analysisId) {

        return analysisService.getAnalysis(analysisId);
    }

    /**
     * GET /api/core/analyses/{analysisId}/status
     *
     * Kiểm tra trạng thái xử lý của Analysis.
     *
     * PENDING
     * PROCESSING
     * COMPLETED
     * FAILED
     */
    @GetMapping("/{analysisId}/status")
    public AnalysisStatus getAnalysisStatus(
            @PathVariable UUID analysisId) {

        return analysisService.getAnalysisStatus(analysisId);
    }
}