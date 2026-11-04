package com.ptit.ai_engine_service.messaging.publisher;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.response.CVAnalysisResult;
import com.ptit.ai_engine_service.dto.response.CVPreprocessingResult;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingResult;
import com.ptit.ai_engine_service.messaging.event.RoutingKeys;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AIMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public AIMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCVPreprocessingResult(
            CVPreprocessingResult result) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RoutingKeys.CV_PREPROCESSING_COMPLETED,
                result);
    }

    public void sendJDPreprocessingResult(
            JDPreprocessingResult result) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RoutingKeys.JD_PREPROCESSING_COMPLETED,
                result);
    }

    public void sendCVAnalysisResult(
            CVAnalysisResult result) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RoutingKeys.CV_ANALYSIS_COMPLETED,
                result);
    }
}