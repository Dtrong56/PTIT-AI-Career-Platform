package com.ptit.ai_engine_service.messaging.consumer;

import com.ptit.ai_engine_service.configs.RabbitMQConfig;
import com.ptit.ai_engine_service.dto.request.CVPreprocessingRequest;
import com.ptit.ai_engine_service.dto.response.CVPreprocessingResult;
import com.ptit.ai_engine_service.messaging.publisher.AIMessagePublisher;
import com.ptit.ai_engine_service.service.preprocessing.cv.CVPreprocessingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CVPreprocessingConsumer {

    // Logger để ghi lại thông tin và lỗi trong quá trình xử lý CV preprocessing
    private static final Logger log = LoggerFactory.getLogger(CVPreprocessingConsumer.class);

    // Các dịch vụ cần thiết để xử lý CV preprocessing và gửi kết quả
    private final CVPreprocessingService preprocessingService;
    private final AIMessagePublisher publisher;

    // Constructor để khởi tạo các dịch vụ cần thiết
    public CVPreprocessingConsumer(
            CVPreprocessingService preprocessingService,
            AIMessagePublisher publisher) {

        this.preprocessingService = preprocessingService;
        this.publisher = publisher;
    }

    // Phương thức để nhận và xử lý các yêu cầu CV preprocessing từ hàng đợi
    // RabbitMQ
    /**
     * Xử lý yêu cầu CV preprocessing từ hàng đợi RabbitMQ.
     * 
     * @param request
     *                Các bước thực hiện:
     *                1. Ghi lại thông tin yêu cầu nhận được.
     *                2. Gọi dịch vụ preprocessing để xử lý yêu cầu.
     *                3. Nếu thành công, ghi lại thông tin hoàn thành và gửi kết
     *                quả.
     *                4. Nếu thất bại, ghi lại lỗi và gửi kết quả thất bại.
     *                5. Gửi kết quả CV preprocessing (thành công hoặc thất bại) đến
     *                hàng đợi RabbitMQ.
     */
    @RabbitListener(queues = RabbitMQConfig.AI_CV_PREPROCESSING_QUEUE)
    public void consume(CVPreprocessingRequest request) {

        // Lấy cvId từ request, nếu request là null thì cvId sẽ là null
        UUID cvId = request != null ? request.cvId() : null;

        log.info(
                "Received CV preprocessing request: cvId={}",
                cvId);

        // Khởi tạo biến result để lưu kết quả của quá trình preprocessing
        CVPreprocessingResult result;

        // Thử thực hiện quá trình preprocessing và xử lý các ngoại lệ nếu có
        try {

            // Gọi dịch vụ preprocessing để xử lý yêu cầu và lưu kết quả vào biến result
            result = preprocessingService.preprocess(request);

            log.info(
                    "CV preprocessing completed: cvId={}",
                    cvId);

        }
        // Nếu có ngoại lệ xảy ra trong quá trình preprocessing, ghi lại lỗi và tạo kết
        // quả thất bại
        catch (Exception exception) {

            log.error(
                    "CV preprocessing failed: cvId={}",
                    cvId,
                    exception);

            // Tạo kết quả thất bại với thông tin lỗi từ ngoại lệ
            result = new CVPreprocessingResult(
                    cvId,
                    null,
                    null,
                    false,
                    exception.getMessage());
        }

        // Gửi kết quả CV preprocessing (thành công hoặc thất bại) đến hàng đợi RabbitMQ
        publisher.sendCVPreprocessingResult(result);
    }
}