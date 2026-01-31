package com.arcm.saga;

import com.acrm.dto.InventoryEvent;
import com.acrm.dto.InventoryStatus;
import com.arcm.saga.entity.Order;
import com.arcm.saga.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.acrm.dto.OrderStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryResultConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "order-group"
    )
    public void consumeInventoryEvent(InventoryEvent event) {

        Order order = orderRepository
                .findById(event.orderId())
                .orElseThrow();

        if (event.status() == InventoryStatus.INVENTORY_RESERVED) {

            order.setStatus(OrderStatus.COMPLETED.name());
            log.info("Order {} COMPLETED", order.getOrderId());

        } else {

            order.setStatus(OrderStatus.CANCELLED_NO_STOCK.name());
            log.info("Order {} CANCELLED (NO STOCK)", order.getOrderId());

        }

        orderRepository.save(order);

    }
}
