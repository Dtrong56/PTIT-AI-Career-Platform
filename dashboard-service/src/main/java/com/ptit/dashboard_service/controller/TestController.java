package com.ptit.dashboard_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class TestController {

    /**
     * Endpoint dùng để kiểm tra Dashboard Service có đang hoạt động hay không.
     *
     * Có thể gọi trực tiếp:
     * GET http://localhost:8084/api/dashboard/test
     *
     * Hoặc sau khi cấu hình API Gateway:
     * GET http://localhost:8080/api/dashboard/test
     */
    @GetMapping("/test")
    public String test() {
        return "Dashboard Service is working!";
    }
}
