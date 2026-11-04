package com.ptit.ai_engine_service.configs;

import com.ptit.ai_engine_service.messaging.event.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EXCHANGE = "ptit.career.exchange";

    // =========================
    // AI REQUEST QUEUES
    // =========================

    public static final String AI_CV_PREPROCESSING_QUEUE = "ai.cv.preprocessing.queue";

    public static final String AI_JD_PREPROCESSING_QUEUE = "ai.jd.preprocessing.queue";

    public static final String AI_ANALYSIS_QUEUE = "ai.analysis.queue";

    @Bean
    public DirectExchange careerExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    // =========================
    // QUEUES
    // =========================

    @Bean
    public Queue aiCvPreprocessingQueue() {
        return QueueBuilder.durable(AI_CV_PREPROCESSING_QUEUE).build();
    }

    @Bean
    public Queue aiJdPreprocessingQueue() {
        return QueueBuilder.durable(AI_JD_PREPROCESSING_QUEUE).build();
    }

    @Bean
    public Queue aiAnalysisQueue() {
        return QueueBuilder.durable(AI_ANALYSIS_QUEUE).build();
    }

    // =========================
    // BINDINGS
    // =========================

    @Bean
    public Binding cvPreprocessingBinding(
            Queue aiCvPreprocessingQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiCvPreprocessingQueue)
                .to(careerExchange)
                .with(RoutingKeys.CV_PREPROCESSING_REQUEST);
    }

    @Bean
    public Binding jdPreprocessingBinding(
            Queue aiJdPreprocessingQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiJdPreprocessingQueue)
                .to(careerExchange)
                .with(RoutingKeys.JD_PREPROCESSING_REQUEST);
    }

    @Bean
    public Binding analysisBinding(
            Queue aiAnalysisQueue,
            DirectExchange careerExchange) {

        return BindingBuilder
                .bind(aiAnalysisQueue)
                .to(careerExchange)
                .with(RoutingKeys.CV_ANALYSIS_REQUEST);
    }

    // =========================
    // JSON MESSAGE CONVERTER
    // =========================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}