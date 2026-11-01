package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.entity.enums.MatchStatus;
import com.ptit.core_platform_service.entity.enums.SkillType;

public record SkillGapResult(
        String skillName,
        SkillType type,
        MatchStatus matchStatus,
        Double score,
        String evidence
) {
}