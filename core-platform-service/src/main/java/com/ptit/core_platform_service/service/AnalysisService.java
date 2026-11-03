package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.dto.analysis.AnalysisResponse;
import com.ptit.core_platform_service.dto.analysis.CreateAnalysisRequest;
import com.ptit.core_platform_service.entity.enums.AnalysisStatus;
import com.ptit.core_platform_service.messaging.CVAnalysisResult;

import java.util.List;
import java.util.UUID;

public interface AnalysisService {

    /**
     * Tạo một yêu cầu phân tích CV - JD.
     *
     * Flow:
     * 1. Validate cvId.
     * 2. Validate jdId.
     * 3. Kiểm tra CV/JD đã preprocessing COMPLETED chưa.
     * 4. Tạo Analysis entity với trạng thái PENDING.
     * 5. Lưu Analysis vào Core DB.
     * 6. Publish CV_ANALYSIS_REQUEST -> RabbitMQ -> AI Engine.
     * 7. Trả về AnalysisResponse với trạng thái PENDING.
     */
    AnalysisResponse createAnalysis(CreateAnalysisRequest request);

    /**
     * Lấy danh sách kết quả phân tích của một sinh viên.
     */
    List<AnalysisResponse> getAnalyses(UUID studentId);

    /**
     * Lấy chi tiết một kết quả phân tích.
     */
    AnalysisResponse getAnalysis(UUID analysisId);

    /**
     * Lấy trạng thái xử lý của một Analysis.
     */
    AnalysisStatus getAnalysisStatus(UUID analysisId);

    /**
     * Xử lý kết quả phân tích trả về từ AI Engine.
     *
     * Flow:
     * RabbitMQ
     * -> CoreMessageConsumer
     * -> AnalysisService
     * -> cập nhật Analysis
     * -> lưu SkillGap
     * -> publish kết quả sang Dashboard
     */
    void handleAnalysisCompleted(CVAnalysisResult result);
}