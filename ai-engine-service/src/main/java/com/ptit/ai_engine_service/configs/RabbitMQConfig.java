package com.ptit.ai_engine_service.configs;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.DirectExchange;
// import org.springframework.amqp.core.Queue;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class RabbitMQConfig {

//     public static final String AI_EXCHANGE = "ai.exchange";

//     public static final String CV_ANALYSIS_QUEUE = "ai.cv.analysis.queue";

//     public static final String AI_RESULT_QUEUE = "ai.result.queue";

//     public static final String CV_ANALYSIS_ROUTING_KEY = "ai.cv.analyze";

//     public static final String AI_RESULT_ROUTING_KEY = "ai.result";

//     @Bean
//     public DirectExchange aiExchange() {
//         return new DirectExchange(AI_EXCHANGE);
//     }

//     @Bean
//     public Queue cvAnalysisQueue() {
//         return new Queue(CV_ANALYSIS_QUEUE, true);
//     }

//     @Bean
//     public Queue aiResultQueue() {
//         return new Queue(AI_RESULT_QUEUE, true);
//     }

//     @Bean
//     public Binding cvAnalysisBinding(
//             Queue cvAnalysisQueue,
//             DirectExchange aiExchange) {

//         return BindingBuilder
//                 .bind(cvAnalysisQueue)
//                 .to(aiExchange)
//                 .with(CV_ANALYSIS_ROUTING_KEY);
//     }

//     @Bean
//     public Binding aiResultBinding(
//             Queue aiResultQueue,
//             DirectExchange aiExchange) {

//         return BindingBuilder
//                 .bind(aiResultQueue)
//                 .to(aiExchange)
//                 .with(AI_RESULT_ROUTING_KEY);
//     }
// }

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
    // 2. QUEUES
    // ============================================================

    // Nhận job phân tích CV/JD từ Core Service
    public static final String AI_ANALYSIS_QUEUE = "ai.analysis.queue";

    // Sau này có thể dùng cho các job Interview
    public static final String AI_INTERVIEW_QUEUE = "ai.interview.queue";

    // ============================================================
    // 3. ROUTING KEYS
    // ============================================================

    public static final String AI_ANALYSIS_ROUTING_KEY = "ai.analysis";

    public static final String AI_INTERVIEW_ROUTING_KEY = "ai.interview";

    // ============================================================
    // 4. EXCHANGE
    // ============================================================

    @Bean
    public DirectExchange aiExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // ============================================================
    // 5. QUEUE - AI ANALYSIS
    // ============================================================

    @Bean
    public Queue aiAnalysisQueue() {
        return new Queue(AI_ANALYSIS_QUEUE, true);
    }

    // ============================================================
    // 6. QUEUE - AI INTERVIEW
    // ============================================================

    @Bean
    public Queue aiInterviewQueue() {
        return new Queue(AI_INTERVIEW_QUEUE, true);
    }

    // ============================================================
    // 7. BINDING - AI ANALYSIS
    // ============================================================

    @Bean
    public Binding aiAnalysisBinding(
            Queue aiAnalysisQueue,
            DirectExchange aiExchange) {

        return BindingBuilder
                .bind(aiAnalysisQueue)
                .to(aiExchange)
                .with(AI_ANALYSIS_ROUTING_KEY);
    }

    // ============================================================
    // 8. BINDING - AI INTERVIEW
    // ============================================================

    @Bean
    public Binding aiInterviewBinding(
            Queue aiInterviewQueue,
            DirectExchange aiExchange) {

        return BindingBuilder
                .bind(aiInterviewQueue)
                .to(aiExchange)
                .with(AI_INTERVIEW_ROUTING_KEY);
    }
}