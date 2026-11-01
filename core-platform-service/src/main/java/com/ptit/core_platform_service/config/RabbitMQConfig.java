package com.ptit.core_platform_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "ptit.career.exchange";

    // =========================
    // AI QUEUES
    // =========================

    public static final String AI_CV_PREPROCESSING_QUEUE = "ai.cv.preprocessing.queue";

    public static final String AI_JD_PREPROCESSING_QUEUE = "ai.jd.preprocessing.queue";

    public static final String AI_ANALYSIS_QUEUE = "ai.analysis.queue";

    // =========================
    // CORE RESULT QUEUES
    // =========================

    public static final String CORE_CV_PREPROCESSING_RESULT_QUEUE = "core.cv.preprocessing.result.queue";

    public static final String CORE_JD_PREPROCESSING_RESULT_QUEUE = "core.jd.preprocessing.result.queue";

    public static final String CORE_ANALYSIS_RESULT_QUEUE = "core.analysis.result.queue";

    // =========================
    // DASHBOARD QUEUES
    // =========================

    public static final String DASHBOARD_CV_ADD_QUEUE = "dashboard.cv.add.queue";

    public static final String DASHBOARD_JD_ADD_QUEUE = "dashboard.jd.add.queue";

    public static final String DASHBOARD_ANALYSIS_RESULT_QUEUE = "dashboard.analysis.result.queue";

    // =========================
    // ROUTING KEYS
    // =========================

    public static final String CV_PREPROCESSING_REQUEST = "cv.preprocessing.request";

    public static final String JD_PREPROCESSING_REQUEST = "jd.preprocessing.request";

    public static final String CV_ANALYSIS_REQUEST = "cv.analysis.request";

    public static final String CV_PREPROCESSING_COMPLETED = "cv.preprocessing.completed";

    public static final String JD_PREPROCESSING_COMPLETED = "jd.preprocessing.completed";

    public static final String CV_ANALYSIS_COMPLETED = "cv.analysis.completed";

    public static final String CV_ADD_REQUEST = "cv.add.request";

    public static final String JD_ADD_REQUEST = "jd.add.request";

    public static final String CV_ANALYSIS_RESULT = "cv.analysis.result";

    @Bean
    public DirectExchange careerExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // =========================
    // QUEUES
    // =========================

    @Bean
    public Queue aiCvPreprocessingQueue() {
        return new Queue(AI_CV_PREPROCESSING_QUEUE, true);
    }

    @Bean
    public Queue aiJdPreprocessingQueue() {
        return new Queue(AI_JD_PREPROCESSING_QUEUE, true);
    }

    @Bean
    public Queue aiAnalysisQueue() {
        return new Queue(AI_ANALYSIS_QUEUE, true);
    }

    @Bean
    public Queue coreCvPreprocessingResultQueue() {
        return new Queue(CORE_CV_PREPROCESSING_RESULT_QUEUE, true);
    }

    @Bean
    public Queue coreJdPreprocessingResultQueue() {
        return new Queue(CORE_JD_PREPROCESSING_RESULT_QUEUE, true);
    }

    @Bean
    public Queue coreAnalysisResultQueue() {
        return new Queue(CORE_ANALYSIS_RESULT_QUEUE, true);
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

    // =========================
    // AI REQUEST BINDINGS
    // =========================

    @Bean
    public Binding cvPreprocessingBinding(
            Queue aiCvPreprocessingQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiCvPreprocessingQueue)
                .to(careerExchange)
                .with(CV_PREPROCESSING_REQUEST);
    }

    @Bean
    public Binding jdPreprocessingBinding(
            Queue aiJdPreprocessingQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiJdPreprocessingQueue)
                .to(careerExchange)
                .with(JD_PREPROCESSING_REQUEST);
    }

    @Bean
    public Binding analysisBinding(
            Queue aiAnalysisQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiAnalysisQueue)
                .to(careerExchange)
                .with(CV_ANALYSIS_REQUEST);
    }

    // =========================
    // CORE RESULT BINDINGS
    // =========================

    @Bean
    public Binding cvPreprocessingResultBinding(
            Queue coreCvPreprocessingResultQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(coreCvPreprocessingResultQueue)
                .to(careerExchange)
                .with(CV_PREPROCESSING_COMPLETED);
    }

    @Bean
    public Binding jdPreprocessingResultBinding(
            Queue coreJdPreprocessingResultQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(coreJdPreprocessingResultQueue)
                .to(careerExchange)
                .with(JD_PREPROCESSING_COMPLETED);
    }

    @Bean
    public Binding analysisResultBinding(
            Queue coreAnalysisResultQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(coreAnalysisResultQueue)
                .to(careerExchange)
                .with(CV_ANALYSIS_COMPLETED);
    }

    // =========================
    // DASHBOARD BINDINGS
    // =========================

    @Bean
    public Binding dashboardCvAddBinding(
            Queue dashboardCvAddQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(dashboardCvAddQueue)
                .to(careerExchange)
                .with(CV_ADD_REQUEST);
    }

    @Bean
    public Binding dashboardJdAddBinding(
            Queue dashboardJdAddQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(dashboardJdAddQueue)
                .to(careerExchange)
                .with(JD_ADD_REQUEST);
    }

    @Bean
    public Binding dashboardAnalysisResultBinding(
            Queue dashboardAnalysisResultQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(dashboardAnalysisResultQueue)
                .to(careerExchange)
                .with(CV_ANALYSIS_RESULT);
    }
}