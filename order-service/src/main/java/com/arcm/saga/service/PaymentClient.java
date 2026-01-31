package com.arcm.saga.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    public void callPaymentService(UUID orderId, double amount) {

        String url =
                "http://localhost:8082/payments/process?amount=" + amount;

        log.info("Calling Payment Service for order {}", orderId);

        restTemplate.postForObject(url, null, String.class);
    }
}
