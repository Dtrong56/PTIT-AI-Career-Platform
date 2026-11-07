package com.ptit.dashboard_service.controller;

import com.ptit.dashboard_service.dto.FacultyDashboardResponse;
import com.ptit.dashboard_service.dto.StudentDashboardResponse;
import com.ptit.dashboard_service.service.DashboardService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ============================================================
    // STUDENT DASHBOARD
    // ============================================================

    @GetMapping("/student")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(
            @RequestHeader("X-User-Id") UUID studentId) {

        return ResponseEntity.ok(
                dashboardService.getStudentDashboard(studentId));
    }

    // ============================================================
    // FACULTY DASHBOARD
    // ============================================================

    @GetMapping("/faculty")
    public ResponseEntity<FacultyDashboardResponse> getFacultyDashboard() {

        FacultyDashboardResponse response = dashboardService.getFacultyDashboard();

        return ResponseEntity.ok(response);
    }
}