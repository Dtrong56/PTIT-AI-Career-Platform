package com.ptit.ai_engine_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class TestController {

    /**
     * Endpoint dùng để kiểm tra AI Engine Service có đang hoạt động hay không.
     *
     * Có thể gọi trực tiếp:
     * GET http://localhost:8083/api/ai/test
     *
     * Hoặc sau khi cấu hình API Gateway:
     * GET http://localhost:8080/api/ai/test
     */
    @GetMapping("/test")
    public String test() {
        return "AI Engine Service is working!";
    }
}
