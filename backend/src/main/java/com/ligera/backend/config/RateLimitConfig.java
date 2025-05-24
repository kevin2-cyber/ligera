package com.ligera.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.Duration;

/**
 * Configuration for rate limiting using Bucket4j
 */
@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.enabled:true}")
    private boolean enabled;

    @Value("${rate.limit.capacity.anonymous:30}")
    private int anonymousCapacity;

    @Value("${rate.limit.capacity.authenticated:60}")
    private int authenticatedCapacity;

    @Value("${rate.limit.refill.duration.minutes:1}")
    private int refillDurationMinutes;

    @Value("${rate.limit.refill.tokens.anonymous:30}")
    private int anonymousRefillTokens;

    @Value("${rate.limit.refill.tokens.authenticated:60}")
    private int authenticatedRefillTokens;

    /**
     * Create bandwidth for anonymous users (not authenticated)
     */
    @Bean
    public Bandwidth anonymousBandwidth() {
        return Bandwidth.classic(anonymousCapacity, 
                Refill.intervally(anonymousRefillTokens, Duration.ofMinutes(refillDurationMinutes)));
    }

    /**
     * Create bandwidth for authenticated users
     */
    @Bean
    public Bandwidth authenticatedBandwidth() {
        return Bandwidth.classic(authenticatedCapacity, 
                Refill.intervally(authenticatedRefillTokens, Duration.ofMinutes(refillDurationMinutes)));
    }

    /**
     * Check if rate limiting is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}

