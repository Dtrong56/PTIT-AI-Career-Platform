package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.config.RabbitMQConfig;
import com.ptit.core_platform_service.service.AnalysisService;
import com.ptit.core_platform_service.service.CVService;
import com.ptit.core_platform_service.service.JDService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoreMessageConsumer {

    private final CVService cvService;
    private final AnalysisService analysisService;
    private final JDService jdService;

    // =========================
    // CV PREPROCESSING RESULT
    // =========================

    @RabbitListener(queues = RabbitMQConfig.CORE_CV_PREPROCESSING_RESULT_QUEUE)
    public void consumeCVPreprocessingResult(
            CVPreprocessingResult result) {

        // 1. Tìm CV theo cvId
        // 2. Kiểm tra kết quả preprocessing
        // 3. Nếu thất bại:
        // - cập nhật status = FAILED
        // - lưu errorMessage nếu cần
        // 4. Nếu thành công:
        // - cập nhật rawText
        // - cập nhật structuredData / normalized data
        // - cập nhật status = COMPLETED
        // 5. Lưu CV

        cvService.handlePreprocessingCompleted(result);
    }

    // =========================
    // JD PREPROCESSING RESULT
    // =========================

    @RabbitListener(queues = RabbitMQConfig.CORE_JD_PREPROCESSING_RESULT_QUEUE)
    public void consumeJDPreprocessingResult(
            JDPreprocessingResult result) {

        /*
         * Flow:
         *
         * AI Engine
         * ↓
         * RabbitMQ
         * ↓
         * CoreMessageConsumer
         * ↓
         * JDService
         *
         * Consumer không tự xử lý database.
         * Business logic nằm trong JDService.
         */

        jdService.handlePreprocessingCompleted(result);
    }

    // =========================
    // ANALYSIS RESULT
    // =========================

    @RabbitListener(queues = RabbitMQConfig.CORE_ANALYSIS_RESULT_QUEUE)
    public void consumeAnalysisResult(
            CVAnalysisResult result) {

        /*
         * Flow:
         *
         * AI Engine
         * ↓
         * RabbitMQ
         * ↓
         * Consumer
         * ↓
         * AnalysisService
         *
         * Consumer KHÔNG tự xử lý database.
         * Business logic nằm trong AnalysisService.
         */

        analysisService.handleAnalysisCompleted(result);
    }
}
