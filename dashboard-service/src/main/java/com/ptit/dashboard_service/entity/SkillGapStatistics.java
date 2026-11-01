package com.ptit.dashboard_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_gap_statistics")
public class SkillGapStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String normalizedSkill;

    private String skillName;

    private Integer count;

    public SkillGapStatistics() {
    }

    public SkillGapStatistics(
            String normalizedSkill,
            String skillName,
            Integer count) {
        this.normalizedSkill = normalizedSkill;
        this.skillName = skillName;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getNormalizedSkill() {
        return normalizedSkill;
    }

    public void setNormalizedSkill(String normalizedSkill) {
        this.normalizedSkill = normalizedSkill;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}