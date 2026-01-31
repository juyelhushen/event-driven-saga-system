package com.arcm.saga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;
    private Integer productId;
    private Double price;
    private String status;
    private LocalDateTime createdAt =  LocalDateTime.now();
    private int retryCount = 0;
}
