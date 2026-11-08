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

                // Không thể tạo kết quả nghiệp vụ nếu không có jdId để đối chiếu.
                if (request == null || request.jdId() == null) {
                        log.error("Received invalid JD preprocessing request: request={}, jdId={}",
                                        request, request != null ? request.jdId() : null);

                        throw new IllegalArgumentException(
                                        "JD preprocessing request and jdId must not be null.");
                }

                log.info(
                                "Received JD preprocessing request: jdId={}",
                                request.jdId());

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
                                        request.jdId(),
                                        exception);

                        String errorMessage = exception.getMessage();

                        if (errorMessage == null || errorMessage.isBlank()) {
                                errorMessage = "Unexpected JD preprocessing error.";
                        }

                        result = new JDPreprocessingResult(
                                        request.jdId(),
                                        null,
                                        null,
                                        false,
                                        errorMessage);
                }

                // Gửi kết quả về Core sau khi đã xác định trạng thái xử lý.
                publisher.sendJDPreprocessingResult(result);
        }
}