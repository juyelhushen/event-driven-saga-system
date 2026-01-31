package com.acrm.saga.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @GetMapping("/test")
    public String test() {
        log.info("testing endpoint reached");
        return "Test successful!";
    }


    @PostMapping("/process")
    public String processPayment(@RequestParam double amount) {
        log.info("Payment service received request for amount {}", amount);

        if (amount == 999)
            throw new RuntimeException("Payment Service DOWN");

        return "PAYMENT_SUCCESS";
    }
}
