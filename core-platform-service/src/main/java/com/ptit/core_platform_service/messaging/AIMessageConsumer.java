package com.ptit.core_platform_service.messaging;

import com.ptit.core_platform_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIMessageConsumer {

    @RabbitListener(queues = RabbitMQConfig.CORE_ANALYSIS_RESULT_QUEUE)
    public void consumeAnalysisResult(CVAnalysisResult result) {

        // TODO:
        // 1. Tìm Analysis
        // 2. Kiểm tra result
        // 3. Cập nhật status
        // 4. Cập nhật matchScore
        // 5. Cập nhật semanticScore
        // 6. Cập nhật skillScore
        // 7. Lưu SkillGap
        // 8. Publish CV_ANALYSIS_RESULT -> Dashboard
    }
}