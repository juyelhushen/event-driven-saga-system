package com.arcm.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentEventsDLQConsumer {

    @KafkaListener(
            topics = "payment-events.DLT",
            groupId = "order-dlq-group"
    )
    public void consumeDLQ(Object payload) {

        log.error(
                "🚨 CRITICAL: Message moved to DLQ: {}",
                payload
        );
    }
}
