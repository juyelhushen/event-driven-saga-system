package com.acrm.dto;

import java.util.UUID;

public record OrderEvent(
        UUID orderId,
        Integer producerId,
        Double amount,
        String status //PENDING, COMPLETED, CANCELLED
) {
}
