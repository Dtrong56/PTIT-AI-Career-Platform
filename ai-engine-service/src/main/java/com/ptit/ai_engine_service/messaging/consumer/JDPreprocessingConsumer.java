
package com.ptit.ai_engine_service.messaging.consumer;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.request.JDPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.JDPreprocessingResult;
import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
import com.ptit.ai_engine_service.service.preprocessing.jd.JDPreprocessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class JDPreprocessingConsumer {

    private static final Logger log = LoggerFactory.getLogger(JDPreprocessingConsumer.class);

    private final JDPreprocessingService preprocessingService;
    private final AIMessagePublisher publisher;

    public JDPreprocessingConsumer(
            JDPreprocessingService preprocessingService,
            AIMessagePublisher publisher) {

        this.preprocessingService = preprocessingService;
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQConfig.AI_JD_PREPROCESSING_QUEUE)
    public void consume(JDPreprocessingRequest request) {

        log.info(
                "Received JD preprocessing request: jdId={}",
                request != null ? request.jdId() : null);

        JDPreprocessingResult result;

        try {
            result = preprocessingService.preprocess(request);

            log.info(
                    "JD preprocessing completed: jdId={}, jdPosition={}",
                    result.jdId(),
                    result.jdPosition());

        } catch (Exception exception) {

            log.error(
                    "JD preprocessing failed: jdId={}",
                    request != null ? request.jdId() : null,
                    exception);

            result = new JDPreprocessingResult(
                    request != null ? request.jdId() : null,
                    null,
                    null,
                    false,
                    exception.getMessage() != null
                            ? exception.getMessage()
                            : "Unexpected JD preprocessing error.");
        }

        publisher.sendJDPreprocessingResult(result);
    }
}
