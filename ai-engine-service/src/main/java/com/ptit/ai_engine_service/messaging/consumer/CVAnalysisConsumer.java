package com.ptit.ai_engine_service.messaging.consumer;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.request.CVAnalysisRequest;
import com.ptit.ai_engine_service.dto.response.CVAnalysisResult;
import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CVAnalysisConsumer {

    private static final Logger log = LoggerFactory.getLogger(CVAnalysisConsumer.class);

    private final AIMessagePublisher publisher;

    public CVAnalysisConsumer(AIMessagePublisher publisher) {
        this.publisher = publisher;
    }

    @RabbitListener(queues = RabbitMQConfig.AI_ANALYSIS_QUEUE)
    public void consume(CVAnalysisRequest request) {

        log.info(
                "Received CV analysis request: analysisId={}, cvId={}, jdId={}",
                request.analysisId(),
                request.cvId(),
                request.jdId());

        CVAnalysisResult result;

        try {
            result = process(request);
        } catch (Exception exception) {
            log.error("CV analysis failed: analysisId={}",
                    request.analysisId(), exception);

            result = new CVAnalysisResult(
                    request.analysisId(),
                    null,
                    null,
                    null,
                    List.of(),
                    false,
                    exception.getMessage());
        }

        publisher.sendCVAnalysisResult(result);
    }

    private CVAnalysisResult process(CVAnalysisRequest request) {

        // TODO: Gọi CV-JD matching pipeline.
        // Pipeline sẽ tính semanticScore, skillScore,
        // matchScore và skillGaps.

        return new CVAnalysisResult(
                request.analysisId(),
                null,
                null,
                null,
                List.of(),
                false,
                "CV analysis pipeline has not been implemented yet.");
    }
}