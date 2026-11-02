package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.dto.cv.CVResponse;
import com.ptit.core_platform_service.dto.cv.CreateCVRequest;
import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import com.ptit.core_platform_service.messaging.CVPreprocessingResult;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface CVService {

    /**
     * Tạo / upload CV mới.
     *
     * Flow:
     * 1. Validate studentId.
     * 2. Validate jdId nếu có.
     * 3. Tạo CV entity từ request.
     * 4. Lưu CV cơ bản vào Core DB.
     * 5. Publish CV_ADD_REQUEST -> RabbitMQ -> Dashboard.
     * 6. Publish CV_PREPROCESSING_REQUEST -> RabbitMQ -> AI Engine.
     * 7. Trả về CVResponse với trạng thái PENDING.
     */
    // CVResponse createCV(CreateCVRequest request);
    CVResponse uploadCV(
            UUID studentId,
            MultipartFile file);

    /**
     * Lấy danh sách CV của một sinh viên.
     *
     * Flow:
     * 1. Validate studentId.
     * 2. Query các CV thuộc student.
     * 3. Mapping Entity -> CVResponse.
     */
    List<CVResponse> getCVs(UUID studentId);

    /**
     * Lấy thông tin một CV.
     *
     * Flow:
     * 1. Tìm CV theo cvId.
     * 2. Nếu không tồn tại -> throw exception.
     * 3. Mapping Entity -> CVResponse.
     */
    CVResponse getCV(UUID cvId);

    /**
     * Lấy trạng thái preprocessing của CV.
     *
     * Flow:
     * 1. Tìm CV theo cvId.
     * 2. Nếu không tồn tại -> throw exception.
     * 3. Trả về ProcessingStatus hiện tại.
     */
    ProcessingStatus getCVStatus(UUID cvId);

    /**
     * Xử lý kết quả preprocessing từ AI Engine.
     *
     * Flow:
     * 1. Tìm CV theo cvId.
     * 2. Kiểm tra kết quả preprocessing.
     * 3. Nếu thất bại:
     * - cập nhật status = FAILED
     * - lưu errorMessage nếu cần
     * 4. Nếu thành công:
     * - cập nhật rawText
     * - cập nhật structuredData / normalized data
     * - cập nhật status = COMPLETED
     * 5. Lưu CV
     */
    void handlePreprocessingCompleted(
            CVPreprocessingResult result);
}
