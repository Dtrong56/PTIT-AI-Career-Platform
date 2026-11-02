package com.ptit.core_platform_service.service.impl;

import com.ptit.core_platform_service.dto.cv.CVResponse;
import com.ptit.core_platform_service.entity.CV;
import com.ptit.core_platform_service.entity.Student;
import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import com.ptit.core_platform_service.messaging.CoreMessageProducer;
import com.ptit.core_platform_service.messaging.CVAddRequest;
import com.ptit.core_platform_service.messaging.CVPreprocessingRequest;
import com.ptit.core_platform_service.messaging.CVPreprocessingResult;
import com.ptit.core_platform_service.repository.CVRepository;
import com.ptit.core_platform_service.repository.StudentRepository;
import com.ptit.core_platform_service.service.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {

        private final CVRepository cvRepository;
        private final StudentRepository studentRepository;
        private final CoreMessageProducer coreMessageProducer;

        /**
         * Upload CV.
         *
         * Flow:
         * 1. Validate file.
         * 2. Kiểm tra student.
         * 3. Tạo CV và lưu metadata.
         * 4. Gửi CV_PREPROCESSING_REQUEST đến AI Engine.
         * 5. Gửi CV_ADD_REQUEST đến Dashboard.
         * 6. Trả thông tin CV cho client.
         */
        @Override
        @Transactional
        public CVResponse uploadCV(
                        UUID studentId,
                        MultipartFile file) {

                // 1. Validate file
                validateCVFile(file);

                // 2. Kiểm tra student tồn tại
                Student student = studentRepository
                                .findById(studentId)
                                .orElseThrow(() -> new RuntimeException("Student not found"));

                // 3. Tạo CV
                LocalDateTime now = LocalDateTime.now();

                CV cv = CV.builder()
                                .student(student)
                                .status(ProcessingStatus.PENDING)
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                // 4. Lưu CV metadata
                CV savedCV = cvRepository.save(cv);

                // 5. Tạo message gửi AI Engine
                try {

                        CVPreprocessingRequest request = new CVPreprocessingRequest(
                                        savedCV.getCvId(),
                                        student.getStudentId(),
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes());

                        // Core -> RabbitMQ -> AI Engine
                        coreMessageProducer.sendCVPreprocessingRequest(
                                        request);

                        // 6. Cập nhật trạng thái đang xử lý
                        savedCV.setStatus(
                                        ProcessingStatus.PROCESSING);
                        savedCV.setUpdatedAt(
                                        LocalDateTime.now());

                        cvRepository.save(savedCV);

                } catch (IOException e) {

                        savedCV.setStatus(
                                        ProcessingStatus.FAILED);
                        savedCV.setUpdatedAt(
                                        LocalDateTime.now());

                        cvRepository.save(savedCV);

                        throw new RuntimeException(
                                        "Cannot read CV file",
                                        e);
                }

                // 7. Thông báo Dashboard có CV mới
                CVAddRequest addRequest = new CVAddRequest(
                                savedCV.getCvId(),
                                student.getStudentId());
                coreMessageProducer.sendCVAddEvent(addRequest);

                // 8. Trả response
                return mapToResponse(savedCV);
        }

        /**
         * Lấy danh sách CV của sinh viên.
         */
        @Override
        @Transactional(readOnly = true)
        public List<CVResponse> getCVs(
                        UUID studentId) {

                return cvRepository
                                .findByStudentId(studentId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        /**
         * Lấy thông tin CV theo ID.
         */
        @Override
        @Transactional(readOnly = true)
        public CVResponse getCV(
                        UUID cvId) {

                CV cv = cvRepository
                                .findById(cvId)
                                .orElseThrow(() -> new RuntimeException("CV not found"));

                return mapToResponse(cv);
        }

        /**
         * Lấy trạng thái xử lý CV.
         */
        @Override
        @Transactional(readOnly = true)
        public ProcessingStatus getCVStatus(
                        UUID cvId) {

                CV cv = cvRepository
                                .findById(cvId)
                                .orElseThrow(() -> new RuntimeException("CV not found"));

                return cv.getStatus();
        }

        /**
         * Xử lý kết quả preprocessing từ AI Engine.
         *
         * Flow:
         * AI Engine
         * ↓
         * CV_PREPROCESSING_COMPLETED
         * ↓
         * CoreMessageConsumer
         * ↓
         * handlePreprocessingCompleted()
         */
        @Override
        @Transactional
        public void handlePreprocessingCompleted(
                        CVPreprocessingResult result) {

                // 1. Tìm CV
                CV cv = cvRepository
                                .findById(result.cvId())
                                .orElseThrow(() -> new RuntimeException("CV not found"));

                // 2. Kiểm tra kết quả từ AI
                if (!result.success()) {

                        cv.setStatus(
                                        ProcessingStatus.FAILED);

                        cv.setUpdatedAt(
                                        LocalDateTime.now());

                        cvRepository.save(cv);

                        return;
                }

                // 3. Cập nhật raw text
                cv.setRawText(
                                result.rawText());

                // 4. Cập nhật structured content
                cv.setStructuredContent(
                                result.structuredData());

                // 5. Cập nhật trạng thái hoàn thành
                cv.setStatus(
                                ProcessingStatus.COMPLETED);

                // 6. Cập nhật thời gian
                cv.setUpdatedAt(
                                LocalDateTime.now());

                // 7. Lưu CV
                cvRepository.save(cv);
        }

        /**
         * Validate file CV.
         */
        private void validateCVFile(
                        MultipartFile file) {

                if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "CV file must not be empty");
                }

                if (!"application/pdf".equalsIgnoreCase(
                                file.getContentType())) {
                        throw new IllegalArgumentException(
                                        "CV file must be PDF");
                }
        }

        /**
         * Mapping CV entity sang CVResponse.
         */
        private CVResponse mapToResponse(
                        CV cv) {

                return new CVResponse(
                                cv.getCvId(),
                                cv.getStudent().getStudentId(),
                                cv.getStatus(),
                                cv.getCreatedAt(),
                                cv.getUpdatedAt());
        }
}