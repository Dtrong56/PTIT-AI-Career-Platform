package com.ptit.ai_engine_service.dto.response;

/**
 * Record này chứa dữ liệu trung gian sau normalization, tách riêng vị trí tuyển
 * dụng và nội dung JD đã cấu trúc.
 */
public record JDPreprocessingData(
        String jdPosition,
        String structuredContent) {
}