package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.dto.jd.CreateJDRequest;
import com.ptit.core_platform_service.dto.jd.JDResponse;

import java.util.List;
import java.util.UUID;

public interface JDService {

    /**
     * Tạo Job Description mới.
     *
     * Flow:
     * 1. Validate studentId.
     * 2. Tạo JD entity từ request.
     * 3. Lưu JD cơ bản vào Core DB.
     * 4. Publish JD_ADD_REQUEST -> RabbitMQ -> Dashboard.
     * 5. Publish JD_PREPROCESSING_REQUEST -> RabbitMQ -> AI Engine.
     * 6. Trả về JDResponse với trạng thái PENDING.
     */
    JDResponse createJD(CreateJDRequest request);

    /**
     * Lấy danh sách JD của một sinh viên.
     *
     * Flow:
     * 1. Validate studentId.
     * 2. Query các JD thuộc student.
     * 3. Mapping Entity -> JDResponse.
     */
    List<JDResponse> getJDs(UUID studentId);

    /**
     * Lấy thông tin một JD.
     *
     * Flow:
     * 1. Tìm JD theo jdId.
     * 2. Nếu không tồn tại -> throw exception.
     * 3. Mapping Entity -> JDResponse.
     */
    JDResponse getJD(UUID jdId);
}
