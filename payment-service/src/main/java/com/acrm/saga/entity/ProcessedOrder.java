package com.acrm.saga.entity;

import com.acrm.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID processOrderId;

    private UUID orderId;

    private PaymentStatus status;

    private LocalDateTime processedAt = LocalDateTime.now();

}
