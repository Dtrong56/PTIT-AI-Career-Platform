
package com.ptit.ai_engine_service.service.skillgap;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class SkillHierarchy {

    /*
     * Key: Skill được yêu cầu trong JD.
     * Value: Các skill rộng hơn có thể được xem là đáp ứng một phần.
     *
     * Ví dụ:
     * JD yêu cầu Spring Boot, CV chỉ có Spring Framework
     * => PARTIAL.
     */
    private static final Map<String, Set<String>> PARENT_SKILLS = Map.of(
            "spring boot", Set.of("spring framework"),
            "typescript", Set.of("javascript"),
            "react native", Set.of("react"));

    public boolean isPartialMatch(
            String jdSkill,
            String cvSkill) {
        if (jdSkill == null || cvSkill == null) {
            return false;
        }

        Set<String> parents = PARENT_SKILLS.get(jdSkill);

        return parents != null && parents.contains(cvSkill);
    }
}
