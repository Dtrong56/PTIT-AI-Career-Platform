package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    // Hàm tìm theo userId
    // List<Student> findByUserId(UUID userId);
    Optional<Student> findByUserId(UUID userId);
}
