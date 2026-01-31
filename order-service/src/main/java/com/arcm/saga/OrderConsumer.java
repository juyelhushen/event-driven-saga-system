package com.arcm.saga;

import com.acrm.dto.PaymentEvent;
import com.acrm.dto.PaymentStatus;
import com.arcm.saga.entity.Order;
import com.acrm.dto.OrderStatus;
import com.arcm.saga.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderRepository orderRepository;

//    @KafkaListener(
//            topics = "payment-events",
//            groupId = "consumer-group"
//    )
    public void consumePaymentResult(PaymentEvent paymentEvent) {

        // ☠️ Poison Pill Simulation

        log.info("Received Payment Event: {}", paymentEvent);

        Order order = orderRepository.findById(paymentEvent.orderId())
                .orElseThrow();

        if (order.getPrice() == 999) {
            log.error("Poison pill detected! amount = 999");
            throw new RuntimeException("Poison pill detected");
        }

        if (PaymentStatus.PAYMENT_APPROVED.equals(paymentEvent.status())) {
            order.setStatus(OrderStatus.COMPLETED.name());
            log.info("Payment completed for order {}", paymentEvent.orderId());
        } else {
            order.setStatus(OrderStatus.CANCELLED.name());
            log.info("Payment cancelled for order {}", paymentEvent.orderId());
        }

        orderRepository.save(order);
    }
}
