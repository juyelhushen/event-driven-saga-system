package com.arcm.saga.service;

import com.arcm.saga.entity.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrchestrator {

    private final PaymentClient paymentClient;

    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "paymentFallback"
    )
    public void processPayment(Order order) {

        log.info("Calling payment service for order {}", order.getOrderId());

        paymentClient.callPaymentService(
                order.getOrderId(),
                order.getPrice()
        );
    }

    public void paymentFallback(Order order, Throwable ex) {
        log.error("CIRCUIT OPEN for order {}", order.getOrderId(), ex);
    }
}

