package com.ptit.core_platform_service.service.impl;

import com.ptit.core_platform_service.messaging.CoreMessageProducer;
import com.ptit.core_platform_service.dto.jd.CreateJDRequest;
import com.ptit.core_platform_service.dto.jd.JDResponse;
import com.ptit.core_platform_service.entity.JD;
import com.ptit.core_platform_service.entity.Student;
import com.ptit.core_platform_service.entity.enums.ProcessingStatus;
import com.ptit.core_platform_service.messaging.JDAddRequest;
import com.ptit.core_platform_service.messaging.JDPreprocessingRequest;
import com.ptit.core_platform_service.repository.JDRepository;
import com.ptit.core_platform_service.repository.StudentRepository;
import com.ptit.core_platform_service.service.JDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JDServiceImpl implements JDService {

    private final JDRepository jdRepository;
    private final StudentRepository studentRepository;
    private final CoreMessageProducer coreMessageProducer;

    /**
     * Tạo Job Description mới.
     *
     * Flow:
     * 1. Kiểm tra student tồn tại.
     * 2. Tạo JD entity.
     * 3. Lưu JD vào Core Database.
     * 4. Gửi JD_ADD_REQUEST cho Dashboard.
     * 5. Gửi JD_PREPROCESSING_REQUEST cho AI Engine.
     * 6. Trả về JDResponse với trạng thái PENDING.
     */
    @Override
    @Transactional
    public JDResponse createJD(CreateJDRequest request) {

        // 1. Kiểm tra student tồn tại
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 2. Tạo JD entity
        JD jd = JD.builder()
                .student(student)
                .title(request.title())
                .rawContent(request.rawContent())
                .status(ProcessingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 3. Lưu JD
        JD savedJD = jdRepository.save(jd);

        // 4. Gửi thông báo JD được thêm vào Dashboard
        coreMessageProducer.sendJDAddEvent(
                new JDAddRequest(
                        savedJD.getJdId(),
                        student.getStudentId()));

        // 5. Gửi JD sang AI Engine để preprocessing
        coreMessageProducer.sendJDPreprocessingRequest(
                new JDPreprocessingRequest(
                        savedJD.getJdId(),
                        savedJD.getRawContent()));

        // 6. Trả response
        return new JDResponse(
                savedJD.getJdId(),
                student.getStudentId(),
                savedJD.getTitle(),
                savedJD.getStatus(),
                savedJD.getCreatedAt(),
                savedJD.getUpdatedAt());
    }

    /**
     * Lấy danh sách JD của sinh viên.
     */
    @Override
    @Transactional(readOnly = true)
    public List<JDResponse> getJDs(UUID studentId) {

        // TODO:
        // 1. Kiểm tra student tồn tại.
        // 2. Query JD theo studentId.
        // 3. Mapping Entity -> Response.

        return jdRepository.findByStudentStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lấy thông tin một JD.
     */
    @Override
    @Transactional(readOnly = true)
    public JDResponse getJD(UUID jdId) {

        JD jd = jdRepository.findById(jdId)
                .orElseThrow(() -> new RuntimeException("JD not found: " + jdId));

        return toResponse(jd);
    }

    /**
     * Mapping JD entity sang JDResponse.
     */
    private JDResponse toResponse(JD jd) {

        return new JDResponse(
                jd.getJdId(),
                jd.getStudent().getStudentId(),
                jd.getTitle(),
                jd.getStatus(),
                jd.getCreatedAt(),
                jd.getUpdatedAt());
    }
}