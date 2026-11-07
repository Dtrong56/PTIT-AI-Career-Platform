package com.ptit.dashboard_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "ptit.career.exchange";

    public static final String DASHBOARD_CV_ADD_QUEUE = "dashboard.cv.add.queue";

    public static final String DASHBOARD_JD_ADD_QUEUE = "dashboard.jd.add.queue";

    public static final String DASHBOARD_ANALYSIS_RESULT_QUEUE = "dashboard.analysis.result.queue";

    public static final String CV_ADD_REQUEST = "cv.add.request";
    public static final String JD_ADD_REQUEST = "jd.add.request";
    public static final String CV_ANALYSIS_RESULT = "cv.analysis.result";

    @Bean
    public DirectExchange careerExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue dashboardCvAddQueue() {
        return new Queue(DASHBOARD_CV_ADD_QUEUE, true);
    }

    @Bean
    public Queue dashboardJdAddQueue() {
        return new Queue(DASHBOARD_JD_ADD_QUEUE, true);
    }

    @Bean
    public Queue dashboardAnalysisResultQueue() {
        return new Queue(DASHBOARD_ANALYSIS_RESULT_QUEUE, true);
    }

    @Bean
    public Binding dashboardCvAddBinding(
            @Qualifier("dashboardCvAddQueue") Queue queue,
            DirectExchange careerExchange) {

        return BindingBuilder.bind(queue)
                .to(careerExchange)
                .with(CV_ADD_REQUEST);
    }

    @Bean
    public Binding dashboardJdAddBinding(
            @Qualifier("dashboardJdAddQueue") Queue queue,
            DirectExchange careerExchange) {

        return BindingBuilder.bind(queue)
                .to(careerExchange)
                .with(JD_ADD_REQUEST);
    }

    @Bean
    public Binding dashboardAnalysisResultBinding(
            @Qualifier("dashboardAnalysisResultQueue") Queue queue,
            DirectExchange careerExchange) {

        return BindingBuilder.bind(queue)
                .to(careerExchange)
                .with(CV_ANALYSIS_RESULT);
    }
}