package com.acrm.dto;

import java.util.UUID;

public record PaymentEvent(
        UUID orderId,
        Integer productId,
        PaymentStatus status
) {

}
