package com.arcm.saga.component;

import com.acrm.dto.OrderEvent;
import com.arcm.saga.entity.OutboxEvent;
import com.arcm.saga.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void publishOutboxEvents() {

        // 🔥 Create ROOT span
        Span span = tracer.nextSpan().name("outbox.publish").start();

        // 🔥 Activate it (THIS IS CRITICAL)
        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {

            List<OutboxEvent> events = outboxEventRepository.findAll();

            log.info("Found {} outbox events to publish", events.size());

            for (OutboxEvent outboxEvent : events) {

                OrderEvent event =
                        objectMapper.readValue(outboxEvent.getPayload(), OrderEvent.class);

                // 🔥 Kafka will inject trace headers automatically
                kafkaTemplate.send(
                        "order-events",
                        outboxEvent.getAggregateId().toString(),
                        event
                );

                outboxEventRepository.delete(outboxEvent);

                log.info(
                        "Published order {} with traceId={}",
                        outboxEvent.getAggregateId(),
                        tracer.currentSpan().context().traceId()
                );
            }

        } catch (Exception e) {
            log.error("Outbox publish failed", e);
        } finally {
            span.end();
        }
    };

}
