package com.ptit.dashboard_service.service;

import com.ptit.dashboard_service.entity.*;
import com.ptit.dashboard_service.messaging.dto.CVAddRequest;
import com.ptit.dashboard_service.messaging.dto.DashboardAnalysisResult;
import com.ptit.dashboard_service.messaging.dto.JDAddRequest;
import com.ptit.dashboard_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class DashboardAggregationService {

    private final StudentDashboardStatsRepository studentDashboardStatsRepository;
    private final StudentPositionMatchStatsRepository studentPositionMatchStatsRepository;
    private final FacultyDashboardStatsRepository facultyDashboardStatsRepository;
    private final SkillGapStatisticsRepository skillGapStatisticsRepository;

    public DashboardAggregationService(
            StudentDashboardStatsRepository studentDashboardStatsRepository,
            StudentPositionMatchStatsRepository studentPositionMatchStatsRepository,
            FacultyDashboardStatsRepository facultyDashboardStatsRepository,
            SkillGapStatisticsRepository skillGapStatisticsRepository) {
        this.studentDashboardStatsRepository = studentDashboardStatsRepository;
        this.studentPositionMatchStatsRepository = studentPositionMatchStatsRepository;
        this.facultyDashboardStatsRepository = facultyDashboardStatsRepository;
        this.skillGapStatisticsRepository = skillGapStatisticsRepository;
    }

    /**
     * Xử lý sự kiện thêm CV.
     * Tăng tổng số CV của sinh viên tương ứng.
     */
    @Transactional
    public void handleCVAdded(CVAddRequest request) {
        if (request == null || request.studentId() == null) {
            return;
        }

        StudentDashboardStats stats = getOrCreateStudentStats(request.studentId());

        stats.setTotalCV(increment(stats.getTotalCV()));

        studentDashboardStatsRepository.save(stats);
    }

    /**
     * Xử lý sự kiện thêm JD.
     * Tăng tổng số JD của sinh viên tương ứng.
     */
    @Transactional
    public void handleJDAdded(JDAddRequest request) {
        if (request == null || request.studentId() == null) {
            return;
        }

        StudentDashboardStats stats = getOrCreateStudentStats(request.studentId());

        stats.setTotalJD(increment(stats.getTotalJD()));

        studentDashboardStatsRepository.save(stats);
    }

    /**
     * Xử lý kết quả phân tích CV-JD thành công.
     *
     * Cập nhật:
     * - Tổng số lượt phân tích của sinh viên.
     * - Số lượt phân tích và điểm match trung bình theo vị trí công việc.
     * - Tổng số lượt phân tích của toàn khoa.
     * - Thống kê tần suất xuất hiện của các kỹ năng trong skillGaps.
     */
    @Transactional
    public void handleAnalysisResult(DashboardAnalysisResult result) {
        if (result == null
                || result.studentId() == null
                || !"COMPLETED".equalsIgnoreCase(result.status())) {
            return;
        }

        // 1. Cập nhật thống kê tổng quan của sinh viên.
        StudentDashboardStats studentStats = getOrCreateStudentStats(result.studentId());

        studentStats.setTotalAnalysis(
                increment(studentStats.getTotalAnalysis()));

        studentDashboardStatsRepository.save(studentStats);

        // 2. Cập nhật thống kê match score theo vị trí công việc.
        updateStudentPositionStats(result);

        // 3. Cập nhật tổng số lượt phân tích của toàn khoa.
        updateFacultyStats();

        // 4. Cập nhật thống kê kỹ năng.
        if (!"COMPLETED".equalsIgnoreCase(result.status())) {
            return;
        }
        updateSkillGapStats(result.skillGaps());
    }

    /**
     * Lấy thống kê sinh viên hiện có hoặc khởi tạo bản ghi mới.
     */
    private StudentDashboardStats getOrCreateStudentStats(UUID studentId) {
        return studentDashboardStatsRepository.findById(studentId)
                .orElseGet(() -> new StudentDashboardStats(
                        studentId,
                        0,
                        0,
                        0));
    }

    /**
     * Cập nhật số lượt phân tích và điểm match trung bình
     * của sinh viên theo từng vị trí công việc.
     */
    private void updateStudentPositionStats(DashboardAnalysisResult result) {
        String jdPosition = result.jdPosition();

        if (jdPosition == null || jdPosition.isBlank()) {
            return;
        }

        if (result.matchScore() == null) {
            return;
        }

        StudentPositionMatchStats stats = studentPositionMatchStatsRepository
                .findByStudentIdAndJdPosition(
                        result.studentId(),
                        jdPosition)
                .orElseGet(() -> new StudentPositionMatchStats(
                        result.studentId(),
                        jdPosition,
                        0,
                        0.0));

        int oldCount = stats.getAnalysisCount() == null
                ? 0
                : stats.getAnalysisCount();

        double oldAverage = stats.getAverageMatchScore() == null
                ? 0.0
                : stats.getAverageMatchScore();

        int newCount = oldCount + 1;

        double newAverage = ((oldAverage * oldCount) + result.matchScore()) / newCount;

        stats.setAnalysisCount(newCount);
        stats.setAverageMatchScore(newAverage);

        studentPositionMatchStatsRepository.save(stats);
    }

    /**
     * Tăng tổng số lượt phân tích CV-JD của toàn khoa.
     */
    private void updateFacultyStats() {
        FacultyDashboardStats stats = facultyDashboardStatsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> new FacultyDashboardStats(0));

        stats.setTotalCVAnalysis(
                increment(stats.getTotalCVAnalysis()));

        facultyDashboardStatsRepository.save(stats);
    }

    /**
     * Cập nhật số lần xuất hiện của từng kỹ năng trong kết quả phân tích.
     * Sử dụng normalizedSkill để tránh lưu trùng kỹ năng do khác chữ hoa,
     * chữ thường hoặc khoảng trắng.
     */
    private void updateSkillGapStats(
            List<DashboardAnalysisResult.SkillGapItem> skillGaps) {

        if (skillGaps == null || skillGaps.isEmpty()) {
            return;
        }

        for (DashboardAnalysisResult.SkillGapItem skillGap : skillGaps) {
            if (skillGap == null || skillGap.skillName() == null
                    || skillGap.skillName().isBlank()) {
                continue;
            }

            String skillName = skillGap.skillName().trim();
            String normalizedSkill = skillName.toLowerCase(Locale.ROOT);

            SkillGapStatistics stats = skillGapStatisticsRepository
                    .findByNormalizedSkill(normalizedSkill)
                    .orElseGet(() -> {
                        SkillGapStatistics newStats = new SkillGapStatistics();
                        newStats.setNormalizedSkill(normalizedSkill);
                        newStats.setSkillName(skillName);
                        newStats.setCount(0);
                        return newStats;
                    });

            stats.setCount(stats.getCount() + 1);
            skillGapStatisticsRepository.save(stats);
        }
    }
    // private void updateSkillGapStats(DashboardAnalysisResult result) {
    // if (result.skillGaps() == null || result.skillGaps().isEmpty()) {
    // return;
    // }

    // for (DashboardAnalysisResult.SkillGapItem skillGap : result.skillGaps()) {
    // if (skillGap == null
    // || skillGap.skillName() == null
    // || skillGap.skillName().isBlank()) {
    // continue;
    // }

    // String skillName = skillGap.skillName().trim();

    // String normalizedSkill = skillName
    // .toLowerCase(Locale.ROOT);

    // SkillGapStatistics stats = skillGapStatisticsRepository
    // .findByNormalizedSkill(normalizedSkill)
    // .orElseGet(() -> new SkillGapStatistics(
    // normalizedSkill,
    // skillName,
    // 0));

    // stats.setCount(increment(stats.getCount()));

    // skillGapStatisticsRepository.save(stats);
    // }
    // }

    /**
     * Tăng giá trị Integer, mặc định từ 0 nếu giá trị hiện tại null.
     */
    private Integer increment(Integer value) {
        return value == null ? 1 : value + 1;
    }
}