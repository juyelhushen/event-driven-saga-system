package com.acrm.saga.repository;

import com.acrm.saga.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, UUID> {
    boolean existsByOrderId(UUID orderId);
    Optional<ProcessedOrder> findByOrderId(UUID orderId);
}
