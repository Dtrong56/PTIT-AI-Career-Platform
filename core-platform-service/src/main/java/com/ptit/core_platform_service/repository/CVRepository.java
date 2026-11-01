package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CVRepository extends JpaRepository<CV, UUID> {

    List<CV> findByStudentStudentId(UUID studentId);
}