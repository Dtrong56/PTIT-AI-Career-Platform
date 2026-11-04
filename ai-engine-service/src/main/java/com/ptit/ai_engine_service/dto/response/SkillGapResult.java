package com.ptit.ai_engine_service.dto.response;

import com.ptit.ai_engine_service.dto.enums.MatchStatus;
import com.ptit.ai_engine_service.dto.enums.SkillType;

public class SkillGapResult {
    String skillName;
    SkillType type;
    MatchStatus matchStatus;
    Double score;
    String evidence;
}