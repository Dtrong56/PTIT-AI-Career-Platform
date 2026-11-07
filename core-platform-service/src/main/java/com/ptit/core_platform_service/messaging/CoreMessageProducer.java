package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.config.RabbitMQConfig;
import com.ptit.core_platform_service.dto.analysis.DashboardAnalysisResult;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoreMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Gửi yêu cầu tiền xử lý JD đến AI Engine.
     *
     * Routing key:
     * JD_PREPROCESSING_REQUEST
     */
    public void sendJDPreprocessingRequest(JDPreprocessingRequest request) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.JD_PREPROCESSING_REQUEST,
                request);
    }

    /**
     * Gửi yêu cầu tiền xử lý CV đến AI Engine.
     *
     * Routing key:
     * CV_PREPROCESSING_REQUEST
     */
    public void sendCVPreprocessingRequest(CVPreprocessingRequest request) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CV_PREPROCESSING_REQUEST,
                request);
    }

    /**
     * Gửi yêu cầu phân tích CV-JD đến AI Engine.
     *
     * Routing key:
     * CV_ANALYSIS_REQUEST
     */
    public void sendAnalysisRequest(CVAnalysisRequest request) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CV_ANALYSIS_REQUEST,
                request);
    }

    /**
     * Thông báo cho Dashboard rằng một CV mới đã được thêm.
     *
     * Routing key:
     * CV_ADD_REQUEST
     */
    public void sendCVAddEvent(Object message) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CV_ADD_REQUEST,
                message);
    }

    /**
     * Thông báo cho Dashboard rằng một JD mới đã được thêm.
     *
     * Routing key:
     * JD_ADD_REQUEST
     */
    public void sendJDAddEvent(Object message) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.JD_ADD_REQUEST,
                message);
    }

    /**
     * Gửi kết quả phân tích đã được Core lưu thành công
     * sang Dashboard Service.
     *
     * Routing key:
     * CV_ANALYSIS_RESULT
     */
    public void sendAnalysisResult(DashboardAnalysisResult result) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CV_ANALYSIS_RESULT,
                result);
    }
}