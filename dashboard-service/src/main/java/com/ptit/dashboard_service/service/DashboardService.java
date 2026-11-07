package com.ptit.dashboard_service.service;

import com.ptit.dashboard_service.dto.FacultyDashboardResponse;
import com.ptit.dashboard_service.dto.StudentDashboardResponse;
import com.ptit.dashboard_service.entity.FacultyDashboardStats;
import com.ptit.dashboard_service.entity.InterviewScoreStatistics;
import com.ptit.dashboard_service.entity.SkillGapStatistics;
import com.ptit.dashboard_service.entity.StudentDashboardStats;
import com.ptit.dashboard_service.repository.FacultyDashboardStatsRepository;
import com.ptit.dashboard_service.repository.InterviewScoreStatisticsRepository;
import com.ptit.dashboard_service.repository.SkillGapStatisticsRepository;
import com.ptit.dashboard_service.repository.StudentDashboardStatsRepository;
import com.ptit.dashboard_service.repository.StudentPositionMatchStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

        private final StudentDashboardStatsRepository studentDashboardStatsRepository;
        private final StudentPositionMatchStatsRepository studentPositionMatchStatsRepository;
        private final FacultyDashboardStatsRepository facultyDashboardStatsRepository;
        private final SkillGapStatisticsRepository skillGapStatisticsRepository;
        private final InterviewScoreStatisticsRepository interviewScoreStatisticsRepository;

        public DashboardService(
                        StudentDashboardStatsRepository studentDashboardStatsRepository,
                        StudentPositionMatchStatsRepository studentPositionMatchStatsRepository,
                        FacultyDashboardStatsRepository facultyDashboardStatsRepository,
                        SkillGapStatisticsRepository skillGapStatisticsRepository,
                        InterviewScoreStatisticsRepository interviewScoreStatisticsRepository) {
                this.studentDashboardStatsRepository = studentDashboardStatsRepository;
                this.studentPositionMatchStatsRepository = studentPositionMatchStatsRepository;
                this.facultyDashboardStatsRepository = facultyDashboardStatsRepository;
                this.skillGapStatisticsRepository = skillGapStatisticsRepository;
                this.interviewScoreStatisticsRepository = interviewScoreStatisticsRepository;
        }

        // ============================================================
        // STUDENT DASHBOARD
        // ============================================================

        @Transactional(readOnly = true)
        public StudentDashboardResponse getStudentDashboard(UUID studentId) {

                StudentDashboardStats stats = studentDashboardStatsRepository
                                .findById(studentId)
                                .orElse(new StudentDashboardStats(
                                                studentId, 0, 0, 0));

                List<StudentDashboardResponse.PositionMatchItem> positionMatches = studentPositionMatchStatsRepository
                                .findByStudentId(studentId)
                                .stream()
                                .map(item -> new StudentDashboardResponse.PositionMatchItem(
                                                item.getJdPosition(),
                                                item.getAnalysisCount(),
                                                item.getAverageMatchScore()))
                                .toList();

                return new StudentDashboardResponse(
                                stats.getTotalCV(),
                                stats.getTotalJD(),
                                stats.getTotalAnalysis(),
                                positionMatches);
        }

        // ============================================================
        // FACULTY DASHBOARD
        // ============================================================

        @Transactional(readOnly = true)
        public FacultyDashboardResponse getFacultyDashboard() {

                // Lấy tổng số CV đã phân tích
                FacultyDashboardStats facultyStats = facultyDashboardStatsRepository
                                .findFirstByOrderByIdAsc()
                                .orElse(null);

                // Lấy Top 5 kỹ năng còn thiếu
                List<SkillGapStatistics> skillGapStats = skillGapStatisticsRepository.findTop5ByOrderByCountDesc();

                List<FacultyDashboardResponse.SkillGapItem> topSkillGaps = skillGapStats.stream()
                                .map(item -> new FacultyDashboardResponse.SkillGapItem(
                                                item.getSkillName(),
                                                item.getCount()))
                                .toList();

                // Lấy điểm phỏng vấn trung bình theo từng vị trí
                List<InterviewScoreStatistics> interviewStats = interviewScoreStatisticsRepository.findAll();

                List<FacultyDashboardResponse.InterviewScoreItem> interviewScores = interviewStats.stream()
                                .map(item -> new FacultyDashboardResponse.InterviewScoreItem(
                                                item.getJdPosition(),
                                                item.getAverageInterviewScore(),
                                                item.getInterviewCount()))
                                .toList();

                return new FacultyDashboardResponse(
                                facultyStats != null ? facultyStats.getTotalCVAnalysis() : 0,
                                topSkillGaps,
                                interviewScores);
        }
}