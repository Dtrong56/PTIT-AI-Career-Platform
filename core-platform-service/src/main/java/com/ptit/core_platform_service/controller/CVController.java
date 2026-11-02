package com.ptit.core_platform_service.controller;

import com.ptit.core_platform_service.dto.cv.CVResponse;
import com.ptit.core_platform_service.dto.cv.CreateCVRequest;
import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import com.ptit.core_platform_service.service.CVService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/cvs")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;

    /**
     * POST /api/core/cvs
     *
     * Flow:
     * Frontend
     * -> API Gateway
     * -> CVController
     * -> CVService
     * -> Save CV
     * |-> CV_ADD_REQUEST -> Dashboard
     * |
     * └-> CV_PREPROCESSING_REQUEST -> AI Engine
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CVResponse uploadCV(
            @RequestParam UUID studentId,
            @RequestPart("file") MultipartFile file) {
        return cvService.uploadCV(studentId, file);
    }

    /**
     * GET /api/core/cvs?studentId={studentId}
     *
     * Lấy danh sách CV của sinh viên.
     */
    @GetMapping
    public List<CVResponse> getCVs(
            @RequestParam UUID studentId) {

        return cvService.getCVs(studentId);
    }

    /**
     * GET /api/core/cvs/{cvId}
     *
     * Lấy thông tin chi tiết của một CV.
     */
    @GetMapping("/{cvId}")
    public CVResponse getCV(
            @PathVariable UUID cvId) {

        return cvService.getCV(cvId);
    }

    /**
     * GET /api/core/cvs/{cvId}/status
     *
     * Kiểm tra trạng thái preprocessing của CV.
     *
     * PENDING
     * PROCESSING
     * COMPLETED
     * FAILED
     */
    @GetMapping("/{cvId}/status")
    public ProcessingStatus getCVStatus(
            @PathVariable UUID cvId) {

        return cvService.getCVStatus(cvId);
    }
}