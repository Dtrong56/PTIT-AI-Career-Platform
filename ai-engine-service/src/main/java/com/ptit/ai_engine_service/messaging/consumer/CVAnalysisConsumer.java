// package com.ptit.ai_engine_service.messaging.consumer;

// import com.ptit.ai_engine_service.configs.RabbitMQConfig;
// import com.ptit.ai_engine_service.dto.request.CVAnalysisRequest;
// import com.ptit.ai_engine_service.dto.response.CVAnalysisResult;
// import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
// import com.ptit.ai_engine_service.service.scoring.CVJDAnalysisService;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;

// import java.util.List;

// @Component
// public class CVAnalysisConsumer {

// private static final Logger log =
// LoggerFactory.getLogger(CVAnalysisConsumer.class);

// private final AIMessagePublisher publisher;
// private final CVJDAnalysisService analysisService;

// public CVAnalysisConsumer(
// AIMessagePublisher publisher,
// CVJDAnalysisService analysisService) {

// this.publisher = publisher;
// this.analysisService = analysisService;
// }

// @RabbitListener(queues = RabbitMQConfig.AI_ANALYSIS_QUEUE)
// public void consume(CVAnalysisRequest request) {

// log.info(
// "Received CV analysis request: analysisId={}, cvId={}, jdId={}",
// request.analysisId(),
// request.cvId(),
// request.jdId());

// CVAnalysisResult result;

// try {
// result = analysisService.analyze(request);

// log.info(
// "CV analysis completed: analysisId={}, matchScore={}",
// request.analysisId(),
// result.matchScore());

// } catch (Exception exception) {

// log.error(
// "CV analysis failed: analysisId={}",
// request.analysisId(),
// exception);

// result = new CVAnalysisResult(
// request.analysisId(),
// null,
// null,
// null,
// List.of(),
// false,
// exception.getMessage());
// }

// publisher.sendCVAnalysisResult(result);
// }
// }