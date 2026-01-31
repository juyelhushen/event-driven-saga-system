package com.acrm.saga;

import com.acrm.dto.OrderEvent;
import com.acrm.dto.PaymentEvent;
import com.acrm.dto.PaymentStatus;
import com.acrm.saga.entity.ProcessedOrder;
import com.acrm.saga.repository.ProcessedOrderRepository;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedOrderRepository processedOrderRepository;

    @KafkaListener(
            topics = "order-events",
            groupId = "payment-group"
    )
    public void consumeOrder(OrderEvent order) throws InterruptedException {

        log.info(
                "Received Order Event, traceId={}",
                MDC.get("traceId")
        );

        Thread.sleep(10000);

        log.info("After sleeping the thread...........................");

        UUID orderId = order.orderId();

        if (processedOrderRepository.existsByOrderId(orderId)) {
            log.warn("IDEMPOTENCY ALERT: Order {} already processed", orderId);
            return;
        }

        PaymentStatus status =
                order.amount() > 100
                        ? PaymentStatus.PAYMENT_REJECTED
                        : PaymentStatus.PAYMENT_APPROVED;

        kafkaTemplate.send(
                "payment-events",
                orderId.toString(),
                new PaymentEvent(orderId,order.producerId(), status)
        );

        ProcessedOrder processedOrder = new ProcessedOrder();
        processedOrder.setProcessedAt(LocalDateTime.now());
        processedOrder.setStatus(status);
        processedOrder.setOrderId(orderId);

        processedOrderRepository.save(processedOrder);
    }
}


