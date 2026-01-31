package com.acrm.dto;

import java.util.UUID;

public record InventoryEvent(
        UUID orderId,
        Integer productId,
        InventoryStatus status
) {}
