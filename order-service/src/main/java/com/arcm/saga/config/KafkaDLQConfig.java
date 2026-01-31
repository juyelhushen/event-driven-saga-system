package com.arcm.saga.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaDLQConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {

        // 🔥 Recoverer → sends message to <original-topic>.DLT
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> {
                            log.error(
                                    "Sending message to DLQ. topic={}, partition={}",
                                    record.topic(),
                                    record.partition(),
                                    ex
                            );
                            return new TopicPartition(
                                    record.topic() + ".DLT",
                                    record.partition()
                            );
                        }
                );

        // 🔁 Retry policy: 2s backoff, 3 total attempts
        // (initial try + 2 retries)
        FixedBackOff backOff = new FixedBackOff(2000L, 2);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}