package com.ptit.core_platform_service.service.impl;

import com.ptit.core_platform_service.dto.analysis.AnalysisResponse;
import com.ptit.core_platform_service.dto.analysis.CreateAnalysisRequest;
import com.ptit.core_platform_service.entity.enums.AnalysisStatus;
import com.ptit.core_platform_service.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    /**
     * Tạo yêu cầu phân tích CV - JD.
     *
     * Flow:
     * 1. Validate CV.
     * 2. Validate JD.
     * 3. Kiểm tra CV/JD đã preprocessing COMPLETED.
     * 4. Tạo Analysis với status = PENDING.
     * 5. Lưu Analysis vào Core DB.
     * 6. Publish CV_ANALYSIS_REQUEST -> RabbitMQ -> AI Engine.
     * 7. Trả về AnalysisResponse.
     */
    @Override
    public AnalysisResponse createAnalysis(CreateAnalysisRequest request) {

        // TODO 1: Tìm CV theo cvId
        // TODO 2: Nếu CV không tồn tại -> throw exception

        // TODO 3: Tìm JD theo jdId
        // TODO 4: Nếu JD không tồn tại -> throw exception

        // TODO 5: Kiểm tra CV status = COMPLETED
        // TODO 6: Kiểm tra JD status = COMPLETED

        // TODO 7: Tạo Analysis entity
        // TODO 8: Set status = PENDING
        // TODO 9: Lưu Analysis vào database

        // TODO 10: Tạo CVAnalysisRequest
        // TODO 11: Publish CV_ANALYSIS_REQUEST -> RabbitMQ -> AI Engine

        // TODO 12: Mapping Analysis entity -> AnalysisResponse

        return null;
    }

    /**
     * Lấy danh sách kết quả phân tích của sinh viên.
     */
    @Override
    public List<AnalysisResponse> getAnalyses(UUID studentId) {

        // TODO 1: Validate studentId
        // TODO 2: Tìm các Analysis liên quan đến CV/JD của student
        // TODO 3: Lấy SkillGap tương ứng
        // TODO 4: Mapping List<Analysis> -> List<AnalysisResponse>

        return null;
    }

    /**
     * Lấy chi tiết một Analysis.
     */
    @Override
    public AnalysisResponse getAnalysis(UUID analysisId) {

        // TODO 1: Tìm Analysis theo analysisId
        // TODO 2: Nếu không tồn tại -> throw exception
        // TODO 3: Lấy danh sách SkillGap
        // TODO 4: Mapping Analysis + SkillGap -> AnalysisResponse

        return null;
    }

    /**
     * Lấy trạng thái xử lý của Analysis.
     */
    @Override
    public AnalysisStatus getAnalysisStatus(UUID analysisId) {

        // TODO 1: Tìm Analysis theo analysisId
        // TODO 2: Nếu không tồn tại -> throw exception
        // TODO 3: Trả về AnalysisStatus

        return null;
    }
}
