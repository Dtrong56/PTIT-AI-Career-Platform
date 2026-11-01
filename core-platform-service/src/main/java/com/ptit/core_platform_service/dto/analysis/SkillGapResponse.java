package com.ptit.core_platform_service.dto.analysis;

import com.ptit.core_platform_service.entity.enums.MatchStatus;
import com.ptit.core_platform_service.entity.enums.SkillType;

import java.util.UUID;

public record SkillGapResponse(
                UUID skillGapId,
                String skillName,
                SkillType type,
                MatchStatus matchStatus,
                Double score,
                String evidence) {
}