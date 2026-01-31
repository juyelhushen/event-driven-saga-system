# 🚀 Saga Pattern & Distributed Systems – Complete Lab Series

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-4.x-black)
![Redis](https://img.shields.io/badge/Redis-Rate%20Limiting-red)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-blue)
![Micrometer](https://img.shields.io/badge/Micrometer-Tracing-purple)

---

## 📑 Table of Contents
1. Project Overview
2. Architecture Overview
3. How to Run Locally (WSL + Kafka + Redis)
4. Lab Breakdown
   - Lab 1: Kafka Basics
   - Lab 2: Order & Payment Saga
   - Lab 3: Idempotent Consumers
   - Lab 4: Dead Letter Queues (DLQ)
   - Lab 5: API Gateway & Rate Limiting
   - Lab 6: Liquibase Migrations
   - Lab 7: Circuit Breakers & Retries
   - Lab 8: Kafka Multi-Broker Cluster
   - Lab 9: Distributed Tracing
   - Lab 10: Inventory Saga & Compensation
5. Architecture Diagrams
6. Interview Q&A Appendix

---

## 📌 Project Overview
This project is a **production-grade distributed system** built using **Spring Boot, Kafka, and modern resilience patterns**. It demonstrates how real-world microservices handle:
- Data consistency without 2PC (Saga Pattern)
- Reliable messaging (Outbox Pattern)
- Failure handling (DLQ, Circuit Breakers)
- Scalability (Kafka consumer groups, multi-broker clusters)
- Observability (Tracing, Metrics)

---

## 🏗️ Architecture Overview

Services:
- Order Service
- Payment Service
- Inventory Service
- API Gateway
- Kafka Cluster (3 brokers)
- Redis (Rate Limiting)

Communication:
- HTTP (Gateway → Services)
- Kafka Events (Service ↔ Service)

---

## ▶️ How to Run Locally (WSL + Kafka + Redis)

### Prerequisites
- Windows + WSL2 (Ubuntu)
- Java 21
- Maven
- Redis

### Kafka (3 Broker Cluster)
```bash
# Start controllers & brokers (KRaft mode)
bin/kafka-server-start.sh config/server-1.properties
bin/kafka-server-start.sh config/server-2.properties
bin/kafka-server-start.sh config/server-3.properties
```

### Create Topics
```bash
bin/kafka-topics.sh --bootstrap-server localhost:9092 \
--create --topic order-events --replication-factor 3 --partitions 3
```

### Redis
```bash
sudo service redis-server start
redis-cli ping
```

### Run Services
```bash
mvn spring-boot:run
```

---

## 🧪 Lab Breakdown

### 🔹 Lab 1 – Kafka Basics
- Topics, partitions, producers, consumers
- Consumer groups

### 🔹 Lab 2 – Order → Payment Saga
- Event-driven Saga
- OrderCreated → PaymentApproved / Rejected

### 🔹 Lab 3 – Idempotent Consumers
- ProcessedOrder table
- Exactly-once logical processing

### 🔹 Lab 4 – Dead Letter Queues (DLQ)
- Retry (3 attempts)
- Poison-pill handling
- payment-events.DLT

### 🔹 Lab 5 – API Gateway & Rate Limiting
- Spring Cloud Gateway
- RedisRateLimiter
- IP-based throttling

### 🔹 Lab 6 – Liquibase Migrations
- DATABASECHANGELOG
- DATABASECHANGELOGLOCK

### 🔹 Lab 7 – Circuit Breakers & Retries
- Resilience4j CircuitBreaker
- Retry & TimeLimiter
- Fallback methods

### 🔹 Lab 8 – Kafka Multi-Broker Cluster
- Replication factor
- ISR
- Broker failure handling

### 🔹 Lab 9 – Distributed Tracing
- Micrometer + OpenTelemetry
- Trace propagation via Kafka headers

### 🔹 Lab 10 – Inventory Saga & Compensation
- Stock reservation
- Compensation on failure

---

## 📊 Architecture Diagrams

### 🔁 Saga Flow
```text
Client
  |
  v
Order Service → Kafka(order-events) → Payment Service
       |                                |
       |                                v
       |<------ Kafka(payment-events) ---
       |
       v
Inventory Service (reserve / compensate)
```

### 📦 Transactional Outbox Pattern
```text
Order Service
+-------------+
| Order Table |
| Outbox Tbl  |
+-------------+
      |
      v
 Outbox Publisher
      |
      v
   Kafka Topic
```

### ☠️ DLQ Flow
```text
Consumer
  |
  | (fails 3 times)
  v
payment-events.DLT
```

---

## 🎤 Interview Q&A Appendix

### Q1. Why Saga instead of 2PC?
**Answer:** 2PC does not scale and blocks resources. Saga ensures eventual consistency using events.

### Q2. What happens if Kafka is down after DB commit?
**Answer:** Outbox Pattern ensures the event is stored and retried safely.

### Q3. Why DLQ?
**Answer:** To isolate poison messages and prevent consumer crashes.

### Q4. Circuit Breaker vs Try-Catch?
**Answer:** Circuit Breaker prevents cascading failures and reduces load during outages.

### Q5. Replication factor 2, lose 2 brokers?
**Answer:** System becomes unavailable (replication-factor − 1 rule).

### Q6. How does Liquibase avoid multiple instances running migrations?
**Answer:** DATABASECHANGELOGLOCK table ensures only one instance runs migrations.

### Q7. What happens if one consumer instance dies?
**Answer:** Kafka rebalances partitions to remaining consumers.

### Q8. Why idempotency is required in Kafka?
**Answer:** Kafka provides at-least-once delivery; consumers must handle duplicates.

### Q9. How is trace propagated across Kafka?
**Answer:** TraceId is stored in message headers and restored in consumers.

### Q10. When do we use compensation?
**Answer:** When a downstream step fails after earlier steps succeeded.

---

## ✅ Final Notes
This project demonstrates **real-world distributed system engineering**, not just tutorials. It covers **failure, scale, resilience, and observability** end-to-end.

📌 **This repo is interview-ready and production-aligned.**

