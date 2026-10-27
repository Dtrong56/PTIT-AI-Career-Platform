package com.ptit.core_platform_service.config;

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
    // Exchange là nơi nhận message từ Producer
    // và quyết định message sẽ đi vào Queue nào.
    //
    // Có thể dùng chung một exchange cho toàn bộ hệ thống.
    // ============================================================

    public static final String EXCHANGE = "ptit.ai.exchange";

    // ============================================================
    // 2. QUEUE
    // ============================================================
    // Queue nhận kết quả từ AI Engine.
    //
    // Ví dụ:
    // AI Engine xử lý CV/JD xong
    // ↓
    // ptit.ai.exchange
    // ↓
    // ai.result
    // ↓
    // core.ai.result.queue
    //
    // Core Service sẽ consume queue này sau này.
    // ============================================================

    public static final String AI_RESULT_QUEUE = "core.ai.result.queue";

    // ============================================================
    // 3. ROUTING KEY
    // ============================================================
    // Routing key giúp Exchange xác định message
    // sẽ được route tới Queue nào.
    // ============================================================

    public static final String AI_RESULT_ROUTING_KEY = "ai.result";

    // ============================================================
    // 4. EXCHANGE BEAN
    // ============================================================

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // ============================================================
    // 5. QUEUE BEAN
    // ============================================================

    @Bean
    public Queue aiResultQueue() {
        return new Queue(AI_RESULT_QUEUE, true);
    }

    // ============================================================
    // 6. BINDING
    // ============================================================
    // Liên kết:
    //
    // Exchange
    // +
    // Routing Key
    // ↓
    // Queue
    //
    // Chưa cần viết Consumer ở bước này.
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
