package com.ptit.dashboard_service.messaging;

import com.ptit.dashboard_service.config.RabbitMQConfig;
import com.ptit.dashboard_service.messaging.dto.CVAddRequest;
import com.ptit.dashboard_service.messaging.dto.DashboardAnalysisResult;
import com.ptit.dashboard_service.messaging.dto.JDAddRequest;
import com.ptit.dashboard_service.service.DashboardAggregationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DashboardMessageConsumer {

    private final DashboardAggregationService aggregationService;

    public DashboardMessageConsumer(
            DashboardAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    /**
     * Nhận sự kiện thêm CV từ Core Service.
     */
    @RabbitListener(queues = RabbitMQConfig.DASHBOARD_CV_ADD_QUEUE)
    public void consumeCVAdded(CVAddRequest request) {
        aggregationService.handleCVAdded(request);
    }

    /**
     * Nhận sự kiện thêm JD từ Core Service.
     */
    @RabbitListener(queues = RabbitMQConfig.DASHBOARD_JD_ADD_QUEUE)
    public void consumeJDAdded(JDAddRequest request) {
        aggregationService.handleJDAdded(request);
    }

    /**
     * Nhận kết quả phân tích CV-JD từ Core Service.
     */
    @RabbitListener(queues = RabbitMQConfig.DASHBOARD_ANALYSIS_RESULT_QUEUE)
    public void consumeAnalysisResult(DashboardAnalysisResult result) {
        aggregationService.handleAnalysisResult(result);
    }
}