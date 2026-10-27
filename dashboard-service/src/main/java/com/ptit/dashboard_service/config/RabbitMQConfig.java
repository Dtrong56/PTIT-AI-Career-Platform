package com.ptit.dashboard_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ============================================================
    // 1. EXCHANGE
    // ============================================================

    public static final String EXCHANGE = "ptit.ai.exchange";

    // ============================================================
    // 2. QUEUE
    // ============================================================
    // Dashboard nhận kết quả AI để xử lý dữ liệu báo cáo.
    // ============================================================

    public static final String AI_RESULT_QUEUE = "dashboard.ai.result.queue";

    // ============================================================
    // 3. ROUTING KEY
    // ============================================================

    public static final String AI_RESULT_ROUTING_KEY = "ai.result";

    // ============================================================
    // 4. EXCHANGE
    // ============================================================

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // ============================================================
    // 5. QUEUE
    // ============================================================

    @Bean
    public Queue aiResultQueue() {
        return new Queue(AI_RESULT_QUEUE, true);
    }

    // ============================================================
    // 6. BINDING
    // ============================================================

    @Bean
    public Binding aiResultBinding(
            Queue aiResultQueue,
            DirectExchange aiExchange) {

        return BindingBuilder
                .bind(aiResultQueue)
                .to(aiExchange)
                .with(AI_RESULT_ROUTING_KEY);
    }
}
