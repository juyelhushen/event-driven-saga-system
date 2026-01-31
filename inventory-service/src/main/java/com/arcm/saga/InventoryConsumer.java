package com.arcm.saga;

import com.acrm.dto.InventoryEvent;
import com.acrm.dto.InventoryStatus;
import com.acrm.dto.PaymentEvent;
import com.acrm.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "payment-events",
            groupId = "inventory-group"
    )
    public void consumePayment(PaymentEvent event) {

        if (event.status() != PaymentStatus.PAYMENT_APPROVED) {
            return;
        }

        InventoryStatus status =
                event.productId() == 99
                        ? InventoryStatus.OUT_OF_STOCK
                        : InventoryStatus.INVENTORY_RESERVED;

        log.info(
                "Inventory check for order {} → {}",
                event.orderId(),
                status
        );

        kafkaTemplate.send(
                "inventory-events",
                new InventoryEvent(
                        event.orderId(),
                        event.productId(),
                        status
                )
        );
    }
}
