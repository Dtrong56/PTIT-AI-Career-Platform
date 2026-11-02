package com.ptit.core_platform_service.controller;

import com.ptit.core_platform_service.dto.jd.CreateJDRequest;
import com.ptit.core_platform_service.dto.jd.JDResponse;
import com.ptit.core_platform_service.service.JDService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/jds")
@RequiredArgsConstructor
public class JDController {

    private final JDService jdService;

    /**
     * POST /api/core/jds
     *
     * Flow:
     * Frontend
     * -> API Gateway
     * -> JDController
     * -> JDService
     * -> Save JD
     * -> JD_PREPROCESSING_REQUEST -> RabbitMQ -> AI Engine
     */
    @PostMapping
    public JDResponse createJD(
            @Valid @RequestBody CreateJDRequest request) {

        return jdService.createJD(request);
    }

    /**
     * GET /api/core/jds?studentId={studentId}
     *
     * Lấy danh sách JD của sinh viên.
     */
    @GetMapping
    public List<JDResponse> getJDs(
            @RequestParam UUID studentId) {

        return jdService.getJDs(studentId);
    }

    /**
     * GET /api/core/jds/{jdId}
     *
     * Lấy thông tin chi tiết của một JD.
     */
    @GetMapping("/{jdId}")
    public JDResponse getJD(
            @PathVariable UUID jdId) {

        return jdService.getJD(jdId);
    }
}