package com.arcm.saga.repository;

import com.arcm.saga.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatusAndCreatedAtBefore(String pending,
                                               LocalDateTime timeoutThreshold);
}
