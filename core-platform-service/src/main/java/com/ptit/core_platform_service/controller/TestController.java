package com.ptit.core_platform_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core")
public class TestController {

    /**
     * Endpoint dùng để kiểm tra Core Platform Service có đang hoạt động hay không.
     *
     * Có thể gọi trực tiếp:
     * GET http://localhost:8082/api/core/test
     *
     * Hoặc sau khi cấu hình API Gateway:
     * GET http://localhost:8080/api/core/test
     */
    @GetMapping("/test")
    public String test() {
        return "Core Platform Service is working!";
    }
}
