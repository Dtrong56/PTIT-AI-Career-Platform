package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.entity.JD;
import com.ptit.core_platform_service.repository.JDRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JDService {

    private final JDRepository jdRepository;

    public JD getById(UUID jdId) {
        return jdRepository.findById(jdId)
                .orElseThrow(() -> new RuntimeException("JD not found: " + jdId));
    }

    public List<JD> getByStudentId(UUID studentId) {
        return jdRepository.findByStudentStudentId(studentId);
    }
}