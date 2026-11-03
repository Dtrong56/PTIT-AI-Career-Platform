package com.ptit.core_platform_service.service;

import com.ptit.core_platform_service.dto.jd.CreateJDRequest;
import com.ptit.core_platform_service.dto.jd.JDResponse;
import com.ptit.core_platform_service.messaging.JDPreprocessingResult;

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

    /**
     * Xử lý kết quả preprocessing JD từ AI Engine.
     *
     * Flow:
     * 1. Nhận JDPreprocessingResult từ CoreMessageConsumer.
     * 2. Tìm JD theo jdId trong Core DB.
     * 3. Kiểm tra trạng thái success của kết quả preprocessing.
     *
     * 4. Nếu preprocessing thất bại:
     * - Cập nhật status = FAILED.
     * - Ghi nhận errorMessage nếu entity hỗ trợ lưu lỗi.
     * - Cập nhật updatedAt.
     * - Lưu JD vào Core DB.
     *
     * 5. Nếu preprocessing thành công:
     * - Nhận structuredContent từ AI Engine.
     * - Cập nhật structuredContent cho JD.
     * - Cập nhật status = COMPLETED.
     * - Cập nhật updatedAt.
     * - Lưu JD vào Core DB.
     *
     * 6. Kết thúc xử lý.
     *
     * Lưu ý:
     * - Core Service chịu trách nhiệm cập nhật trạng thái và dữ liệu JD.
     * - Consumer chỉ nhận message và chuyển kết quả cho JDService.
     * - Không gửi lại request sang AI trong method này.
     */
    void handlePreprocessingCompleted(JDPreprocessingResult result);
}
