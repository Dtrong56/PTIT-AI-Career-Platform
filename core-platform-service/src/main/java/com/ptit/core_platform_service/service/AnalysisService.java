package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.dto.analysis.AnalysisResponse;
import com.ptit.core_platform_service.dto.analysis.CreateAnalysisRequest;
import com.ptit.core_platform_service.entity.enums.AnalysisStatus;

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
     *
     * Flow:
     * 1. Validate studentId.
     * 2. Query các Analysis liên quan đến CV/JD của student.
     * 3. Mapping Entity -> AnalysisResponse.
     * 4. Bao gồm Match Score và Skill Gap nếu đã hoàn thành.
     */
    List<AnalysisResponse> getAnalyses(UUID studentId);

    /**
     * Lấy chi tiết một kết quả phân tích.
     *
     * Flow:
     * 1. Tìm Analysis theo analysisId.
     * 2. Nếu không tồn tại -> throw exception.
     * 3. Lấy danh sách SkillGap của Analysis.
     * 4. Mapping Entity -> AnalysisResponse.
     */
    AnalysisResponse getAnalysis(UUID analysisId);

    /**
     * Lấy trạng thái xử lý của một Analysis.
     *
     * Flow:
     * 1. Tìm Analysis theo analysisId.
     * 2. Nếu không tồn tại -> throw exception.
     * 3. Trả về AnalysisStatus hiện tại.
     */
    AnalysisStatus getAnalysisStatus(UUID analysisId);
}