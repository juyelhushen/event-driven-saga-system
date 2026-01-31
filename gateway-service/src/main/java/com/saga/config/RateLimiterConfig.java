package com.saga.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;


@Configuration
public class RateLimiterConfig {


    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst("X-Forwarded-For");

            if (ip == null) {
                ip = exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress();
            }

            // Normalize IPv6 localhost to IPv4
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }

            System.out.println("Rate limit key = " + ip);
            return Mono.just(ip);
        };
    }


    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // replenishRate = 1 request per second
        // burstCapacity = 3
        return new RedisRateLimiter(1,1, 3);
    }
}