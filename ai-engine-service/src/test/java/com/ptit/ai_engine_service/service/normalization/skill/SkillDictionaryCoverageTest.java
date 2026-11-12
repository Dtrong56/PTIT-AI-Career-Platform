package com.ptit.ai_engine_service.service.normalization.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SkillDictionaryCoverageTest {

    private final SkillDictionary dictionary;

    SkillDictionaryCoverageTest() throws Exception {
        // Constructor ném lỗi nếu có tên/alias trùng nhau, nên test này cũng bắt lỗi
        // trùng.
        this.dictionary = new SkillDictionary(new ObjectMapper());
    }

    @Test
    void commonSkillsMustExist() {
        List<String> required = List.of(
                "Java", "JavaScript", "TypeScript", "Python",
                "Spring", "Spring Boot", "React", "Node.js",
                "MySQL", "PostgreSQL", "MongoDB", "Redis",
                "Docker", "Git", "REST API", "SQL");

        for (String name : required) {
            assertNotNull(dictionary.find(name), "Missing skill in dictionary: " + name);
        }
    }

    @Test
    void aliasesResolveToCanonicalName() {
        assertEquals("JavaScript", dictionary.find("JS").name());
        assertEquals("Spring Framework", dictionary.find("Spring").name());
        assertEquals("REST API", dictionary.find("RESTful").name());
    }
}