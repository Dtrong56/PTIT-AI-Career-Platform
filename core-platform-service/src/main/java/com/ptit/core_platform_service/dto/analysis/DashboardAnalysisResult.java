package com.ptit.core_platform_service.dto.analysis;

import com.ptit.core_platform_service.entity.enums.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DashboardAnalysisResult(

                UUID analysisId,

                UUID studentId,

                UUID cvId,

                UUID jdId,

                String jdPosition,

                AnalysisStatus status,

                Double matchScore,

                Double semanticScore,

                Double skillScore,

                LocalDateTime createdAt,

                LocalDateTime completedAt,

                List<SkillGapResponse> skillGaps

) {
}