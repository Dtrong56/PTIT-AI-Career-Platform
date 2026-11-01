package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.entity.CV;
import com.ptit.core_platform_service.repository.CVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CVRepository cvRepository;

    public CV getById(UUID cvId) {
        return cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found: " + cvId));
    }

    public List<CV> getByStudentId(UUID studentId) {
        return cvRepository.findByStudentStudentId(studentId);
    }
}