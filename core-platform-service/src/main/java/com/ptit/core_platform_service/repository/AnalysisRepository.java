package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    List<Analysis> findByCvId(UUID cvId);

    List<Analysis> findByJdId(UUID jdId);

    @Query("""
            SELECT a
            FROM Analysis a
            JOIN a.cv cv
            JOIN cv.student student
            WHERE student.studentId = :studentId
            ORDER BY a.createdAt DESC
            """)
    List<Analysis> findByStudentId(
            @Param("studentId") UUID studentId);
}