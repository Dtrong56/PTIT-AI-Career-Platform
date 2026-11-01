package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.JD;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JDRepository extends JpaRepository<JD, UUID> {

    List<JD> findByStudentStudentId(UUID studentId);
}