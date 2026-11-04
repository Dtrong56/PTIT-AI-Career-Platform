package com.ptit.ai_engine_service.messaging.consumer;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.request.JDPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingResult;
import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class JDPreprocessingConsumer {

    private static final Logger log = LoggerFactory.getLogger(JDPreprocessingConsumer.class);

    private final AIMessagePublisher publisher;

    public JDPreprocessingConsumer(AIMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQConfig.AI_JD_PREPROCESSING_QUEUE)
    public void consume(JDPreprocessingRequest request) {

        log.info("Received JD preprocessing request: jdId={}",
                request.jdId());

        JDPreprocessingResult result;

        try {
            result = process(request);
        } catch (Exception exception) {
            log.error("JD preprocessing failed: jdId={}",
                    request.jdId(), exception);

            result = new JDPreprocessingResult(
                    request.jdId(),
                    null,
                    false,
                    exception.getMessage());
        }

        publisher.sendJDPreprocessingResult(result);
    }

    private JDPreprocessingResult process(
            JDPreprocessingRequest request) {

        // TODO: Gọi JD preprocessing pipeline.
        // Pipeline sẽ phân tích, trích xuất và chuẩn hóa JD.

        return new JDPreprocessingResult(
                request.jdId(),
                null,
                false,
                "JD preprocessing pipeline has not been implemented yet.");
    }
}