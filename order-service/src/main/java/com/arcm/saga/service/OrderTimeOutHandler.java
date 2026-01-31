package com.arcm.saga.service;

import com.acrm.dto.OrderEvent;
import com.arcm.saga.entity.Order;
import com.acrm.dto.OrderStatus;
import com.arcm.saga.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeOutHandler {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Scheduled(fixedRate = 30000)
    public void checkPendingOrders() {
        log.info("Saga Watchdog : Checking pending orders");
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusSeconds(60);
        List<Order> stuckOrder = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING.name(), timeoutThreshold);

        List<Order> orderList = new ArrayList<>();
        List<OrderEvent> orderEvents = new ArrayList<>();

        log.info("found {} stuck orders", stuckOrder.size());

        if (!stuckOrder.isEmpty()) {
            stuckOrder.forEach(order -> {

                if (order.getRetryCount() < 3) {
                    order.setRetryCount(order.getRetryCount() + 1);  // increamenting

                    //order event
                    OrderEvent orderEvent = new OrderEvent(
                            order.getOrderId(),
                            order.getProductId(),
                            order.getPrice(),
                            OrderStatus.ORDER_CREATED.name()
                    );

                    orderEvents.add(orderEvent);
                    orderList.add(order);

                } else {
                    log.info("cancelling the order due to payment service not available");
                    order.setStatus(OrderStatus.CANCELLED.name());
                    orderList.add(order);
                }
            });
        }

        if (!orderEvents.isEmpty()) {
            log.info("retrying the event in the queue: ");
            orderEvents.forEach(orderEvent -> {
                kafkaTemplate.send("order-events", orderEvent);
            });
        }

        if (!orderList.isEmpty()) orderRepository.saveAll(orderList);
    }
}
