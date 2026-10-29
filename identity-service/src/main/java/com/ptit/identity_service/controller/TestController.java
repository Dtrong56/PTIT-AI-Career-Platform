package com.ptit.identity_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class TestController {

    /**
     * Endpoint dùng để kiểm tra Identity Service có đang hoạt động hay không.
     *
     * Có thể gọi trực tiếp:
     * GET http://localhost:8081/api/auth/test
     *
     * Hoặc sau khi cấu hình API Gateway:
     * GET http://localhost:8080/api/auth/test
     */
    @GetMapping("/test")
    public String test() {
        return "Identity Service is working!";
    }
}
