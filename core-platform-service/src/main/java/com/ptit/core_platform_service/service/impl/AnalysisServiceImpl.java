package com.ptit.core_platform_service.service.impl;

import com.ptit.core_platform_service.dto.analysis.AnalysisResponse;
import com.ptit.core_platform_service.dto.analysis.CreateAnalysisRequest;
import com.ptit.core_platform_service.dto.analysis.DashboardAnalysisResult;
import com.ptit.core_platform_service.dto.analysis.SkillGapResponse;
import com.ptit.core_platform_service.entity.Analysis;
import com.ptit.core_platform_service.entity.CV;
import com.ptit.core_platform_service.entity.JD;
import com.ptit.core_platform_service.entity.SkillGap;
import com.ptit.core_platform_service.entity.enums.AnalysisStatus;
import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import com.ptit.core_platform_service.messaging.CVAnalysisRequest;
import com.ptit.core_platform_service.messaging.CVAnalysisResult;
import com.ptit.core_platform_service.messaging.CoreMessageProducer;
import com.ptit.core_platform_service.messaging.SkillGapResult;
import com.ptit.core_platform_service.repository.AnalysisRepository;
import com.ptit.core_platform_service.repository.CVRepository;
import com.ptit.core_platform_service.repository.JDRepository;
import com.ptit.core_platform_service.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

        private final AnalysisRepository analysisRepository;
        private final CVRepository cvRepository;
        private final JDRepository jdRepository;
        private final CoreMessageProducer messageProducer;

        /**
         * Tạo yêu cầu phân tích CV - JD.
         *
         * Flow:
         * 1. Tìm CV.
         * 2. Tìm JD.
         * 3. Kiểm tra CV/JD đã preprocessing COMPLETED.
         * 4. Tạo Analysis = PENDING.
         * 5. Lưu Analysis.
         * 6. Gửi CVAnalysisRequest sang AI Engine.
         * 7. Trả response PENDING cho frontend.
         */
        @Override
        @Transactional
        public AnalysisResponse createAnalysis(
                        CreateAnalysisRequest request) {

                // ==========================================
                // 1. Tìm CV
                // ==========================================

                CV cv = cvRepository.findById(request.cvId())
                                .orElseThrow(() -> new RuntimeException(
                                                "CV not found: " + request.cvId()));

                // ==========================================
                // 2. Tìm JD
                // ==========================================

                JD jd = jdRepository.findById(request.jdId())
                                .orElseThrow(() -> new RuntimeException(
                                                "JD not found: " + request.jdId()));

                // ==========================================
                // 3. Kiểm tra preprocessing
                // ==========================================

                if (cv.getStatus() != ProcessingStatus.COMPLETED) {
                        throw new IllegalStateException("CV has not completed preprocessing");
                }

                if (jd.getStatus() != ProcessingStatus.COMPLETED) {
                        throw new IllegalStateException("JD has not completed preprocessing");
                }

                if (cv.getStructuredContent() == null || cv.getStructuredContent().isBlank()) {
                        throw new IllegalStateException("CV structured content is empty");
                }

                if (jd.getStructuredContent() == null || jd.getStructuredContent().isBlank()) {
                        throw new IllegalStateException("JD structured content is empty");
                }

                // ==========================================
                // 4. Tạo Analysis
                // ==========================================

                Analysis analysis = Analysis.builder()
                                .cv(cv)
                                .jd(jd)
                                .status(AnalysisStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();

                // ==========================================
                // 5. Lưu Analysis trước
                // ==========================================

                analysis = analysisRepository.save(analysis);

                // ==========================================
                // 6. Gửi request sang AI Engine
                // ==========================================
                CVAnalysisRequest analysisRequest = new CVAnalysisRequest(
                                analysis.getAnalysisId(),
                                cv.getCvId(),
                                jd.getJdId(),
                                cv.getStructuredContent(),
                                jd.getStructuredContent());

                messageProducer.sendAnalysisRequest(analysisRequest);

                // ==========================================
                // 7. Trả response PENDING
                // ==========================================

                return toResponse(analysis);
        }

        /**
         * Lấy lịch sử phân tích của sinh viên.
         */
        @Override
        @Transactional(readOnly = true)
        public List<AnalysisResponse> getAnalyses(
                        UUID studentId) {

                if (studentId == null) {
                        throw new IllegalArgumentException(
                                        "studentId must not be null");
                }

                List<Analysis> analyses = analysisRepository.findByStudentId(studentId);

                return analyses.stream()
                                .map(this::toResponse)
                                .toList();
        }

        /**
         * Lấy chi tiết Analysis.
         */
        @Override
        @Transactional(readOnly = true)
        public AnalysisResponse getAnalysis(
                        UUID analysisId) {

                Analysis analysis = analysisRepository.findById(analysisId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Analysis not found: "
                                                                + analysisId));

                return toResponse(analysis);
        }

        /**
         * Lấy trạng thái Analysis.
         */
        @Override
        @Transactional(readOnly = true)
        public AnalysisStatus getAnalysisStatus(
                        UUID analysisId) {

                Analysis analysis = analysisRepository.findById(analysisId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Analysis not found: "
                                                                + analysisId));

                return analysis.getStatus();
        }

        /**
         * Xử lý kết quả phân tích từ AI Engine.
         *
         * Flow:
         *
         * AI Engine
         * ↓
         * RabbitMQ
         * ↓
         * CoreMessageConsumer
         * ↓
         * handleAnalysisCompleted()
         * ↓
         * Tìm Analysis
         * ↓
         * Nếu FAILED -> Analysis = FAILED
         * ↓
         * Nếu SUCCESS:
         * - lưu matchScore
         * - lưu semanticScore
         * - lưu skillScore
         * - lưu SkillGap
         * - Analysis = COMPLETED
         * ↓
         * Publish kết quả sang Dashboard
         */
        @Override
        @Transactional
        public void handleAnalysisCompleted(
                        CVAnalysisResult result) {

                // ==========================================
                // 1. Tìm Analysis
                // ==========================================

                Analysis analysis = analysisRepository.findById(result.analysisId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Analysis not found: "
                                                                + result.analysisId()));

                // ==========================================
                // 2. Kiểm tra kết quả từ AI Engine
                // ==========================================

                if (!Boolean.TRUE.equals(result.success())) {

                        analysis.setStatus(
                                        AnalysisStatus.FAILED);

                        analysisRepository.save(analysis);

                        // Không publish kết quả thành công
                        // sang Dashboard.
                        return;
                }

                // ==========================================
                // 3. Cập nhật các score
                // ==========================================

                analysis.setMatchScore(
                                result.matchScore());

                analysis.setSemanticScore(
                                result.semanticScore());

                analysis.setSkillScore(
                                result.skillScore());

                // ==========================================
                // 4. Xóa SkillGap cũ nếu có
                // ==========================================

                analysis.getSkillGaps().clear();

                // ==========================================
                // 5. Lưu SkillGap từ AI Engine
                // ==========================================

                if (result.skillGaps() != null) {

                        for (SkillGapResult gapResult : result.skillGaps()) {

                                SkillGap skillGap = SkillGap.builder()
                                                .analysis(analysis)
                                                .skillName(gapResult.skillName())
                                                .type(gapResult.type())
                                                .matchStatus(gapResult.matchStatus())
                                                .score(gapResult.score())
                                                .evidence(gapResult.evidence())
                                                .build();

                                analysis.getSkillGaps()
                                                .add(skillGap);
                        }
                }

                // ==========================================
                // 6. Cập nhật trạng thái COMPLETED
                // ==========================================

                analysis.setStatus(
                                AnalysisStatus.COMPLETED);

                analysis.setCompletedAt(
                                LocalDateTime.now());

                // ==========================================
                // 7. Lưu toàn bộ Analysis + SkillGap
                // ==========================================

                analysis = analysisRepository.save(analysis);

                // ==========================================
                // 8. Gửi kết quả đã được Core xử lý
                // sang Dashboard
                // ==========================================

                messageProducer.sendAnalysisResult(
                                toDashboardResult(analysis));
        }

        /**
         * Mapping Analysis Entity -> AnalysisResponse.
         */
        private AnalysisResponse toResponse(
                        Analysis analysis) {

                List<SkillGapResponse> skillGaps = analysis.getSkillGaps() == null
                                ? Collections.emptyList()
                                : analysis.getSkillGaps()
                                                .stream()
                                                .map(this::toSkillGapResponse)
                                                .toList();

                return new AnalysisResponse(
                                analysis.getAnalysisId(),
                                analysis.getCv().getCvId(),
                                analysis.getJd().getJdId(),
                                analysis.getStatus(),
                                analysis.getMatchScore(),
                                analysis.getSemanticScore(),
                                analysis.getSkillScore(),
                                analysis.getCreatedAt(),
                                analysis.getCompletedAt(),
                                skillGaps);
        }

        /**
         * Mapping SkillGap Entity -> SkillGapResponse.
         */
        private SkillGapResponse toSkillGapResponse(
                        SkillGap skillGap) {

                return new SkillGapResponse(
                                skillGap.getSkillGapId(),
                                skillGap.getSkillName(),
                                skillGap.getType(),
                                skillGap.getMatchStatus(),
                                skillGap.getScore(),
                                skillGap.getEvidence());
        }

        /**
         * Mapping Analysis Entity -> DashboardAnalysisResult.
         *
         * Bổ sung:
         * - studentId: lấy thông qua CV -> Student.
         * - jdPosition: lấy từ JD.
         */
        private DashboardAnalysisResult toDashboardResult(
                        Analysis analysis) {

                List<SkillGapResponse> skillGaps = analysis.getSkillGaps() == null
                                ? Collections.emptyList()
                                : analysis.getSkillGaps()
                                                .stream()
                                                .map(this::toSkillGapResponse)
                                                .toList();

                return new DashboardAnalysisResult(
                                analysis.getAnalysisId(),

                                // CV -> Student -> studentId
                                analysis.getCv()
                                                .getStudent()
                                                .getStudentId(),

                                analysis.getCv().getCvId(),

                                analysis.getJd().getJdId(),

                                // Lấy vị trí công việc từ JD
                                analysis.getJd().getJdPosition(),

                                analysis.getStatus(),

                                analysis.getMatchScore(),

                                analysis.getSemanticScore(),

                                analysis.getSkillScore(),

                                analysis.getCreatedAt(),

                                analysis.getCompletedAt(),

                                skillGaps);
        }
}