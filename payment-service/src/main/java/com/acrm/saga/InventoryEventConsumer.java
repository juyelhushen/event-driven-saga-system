package com.acrm.saga;

import com.acrm.dto.InventoryEvent;
import com.acrm.dto.InventoryStatus;
import com.acrm.dto.PaymentStatus;
import com.acrm.saga.entity.ProcessedOrder;
import com.acrm.saga.repository.ProcessedOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final ProcessedOrderRepository processedOrderRepository;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "payment-group"
    )
    public void consumeInventoryEvent(InventoryEvent event) {

        if (event.status() != InventoryStatus.OUT_OF_STOCK) {
            return;
        }

        ProcessedOrder payment = processedOrderRepository
                .findByOrderId(event.orderId())
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.PAYMENT_APPROVED) {

            log.warn(
                    "REVERSING PAYMENT for Order : {} due to inventory failure",
                    event.orderId()
            );

            payment.setStatus(PaymentStatus.REFUNDED);
            processedOrderRepository.save(payment);
        }
    }
}
