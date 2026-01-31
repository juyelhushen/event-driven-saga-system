package com.arcm.saga.service;

import com.acrm.dto.OrderEvent;
import com.arcm.saga.entity.Order;
import com.acrm.dto.OrderStatus;
import com.arcm.saga.entity.OutboxEvent;
import com.arcm.saga.repository.OrderRepository;
import com.arcm.saga.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentOrchestrator paymentOrchestrator;


    @Transactional
    public Order placeOrder(Order order) throws JsonProcessingException {

        order.setStatus(OrderStatus.PENDING.name());
        if (order.getCreatedAt() == null) order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // 🔥 Circuit breaker protected call
//        paymentOrchestrator.processPayment(savedOrder);

        OrderEvent orderEvent = new OrderEvent(
                savedOrder.getOrderId(),
                savedOrder.getProductId(),
                savedOrder.getPrice(),
                OrderStatus.ORDER_CREATED.name()
        );

        OutboxEvent event = new OutboxEvent();
        event.setPayload(objectMapper.writeValueAsString(orderEvent));
        event.setCreatedAt(LocalDateTime.now());
        event.setAggregateId(savedOrder.getOrderId());

        outboxEventRepository.save(event);
//        kafkaTemplate.send("order-events", orderEvent);
        return savedOrder;
    }


    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }



}
