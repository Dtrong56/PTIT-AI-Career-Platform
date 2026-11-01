package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendAnalysisRequest(CVAnalysisRequest request) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.CV_ANALYSIS_REQUEST,
                request);
    }
}