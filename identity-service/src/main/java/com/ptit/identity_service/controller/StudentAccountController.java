
package com.ptit.identity_service.controller;

import com.ptit.identity_service.dto.CreateStudentAccountRequest;
import com.ptit.identity_service.service.StudentAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty/students")
public class StudentAccountController {

    private final StudentAccountService studentAccountService;

    public StudentAccountController(
            StudentAccountService studentAccountService) {
        this.studentAccountService = studentAccountService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FACULTY')")
    public ResponseEntity<Void> createStudent(
            @RequestBody CreateStudentAccountRequest request) {

        studentAccountService.createStudentAccount(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{studentId}/reset-password")
    @PreAuthorize("hasAuthority('FACULTY')")
    public ResponseEntity<Void> resetPassword(
           @PathVariable("studentId") String studentId) {

        studentAccountService.resetStudentPassword(studentId);
        return ResponseEntity.noContent().build();
    }
}
