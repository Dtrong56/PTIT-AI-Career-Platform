package com.ptit.ai_engine_service.messaging.consumer;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.request.CVPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.CVPreprocessingResult;
import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CVPreprocessingConsumer {

    private static final Logger log = LoggerFactory.getLogger(CVPreprocessingConsumer.class);

    private final AIMessagePublisher publisher;

    public CVPreprocessingConsumer(AIMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQConfig.AI_CV_PREPROCESSING_QUEUE)
    public void consume(CVPreprocessingRequest request) {

        log.info("Received CV preprocessing request: cvId={}",
                request.cvId());

        CVPreprocessingResult result;

        try {
            result = process(request);
        } catch (Exception exception) {
            log.error("CV preprocessing failed: cvId={}",
                    request.cvId(), exception);

            result = new CVPreprocessingResult(
                    request.cvId(),
                    null,
                    null,
                    false,
                    exception.getMessage());
        }

        publisher.sendCVPreprocessingResult(result);
    }

    private CVPreprocessingResult process(
            CVPreprocessingRequest request) {

        // TODO: Gọi CV preprocessing pipeline.
        // Pipeline sẽ trích xuất PDF, cấu trúc và chuẩn hóa CV.

        return new CVPreprocessingResult(
                request.cvId(),
                null,
                null,
                false,
                "CV preprocessing pipeline has not been implemented yet.");
    }
}