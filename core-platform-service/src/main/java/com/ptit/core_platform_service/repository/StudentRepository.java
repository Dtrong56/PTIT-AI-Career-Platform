package com.ptit.core_platform_service.repository;

import com.ptit.core_platform_service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}