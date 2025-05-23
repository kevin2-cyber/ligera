package com.ligera.backend.filter;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Custom actuator endpoint for detailed application metrics
 */
@Component
@Endpoint(id = "appmetrics")
public class MetricsEndpoint {

    private final MetricsFilter metricsFilter;

    public MetricsEndpoint(MetricsFilter metricsFilter) {
        this.metricsFilter = metricsFilter;
    }

    /**
     * Expose detailed application metrics through Spring Boot Actuator
     */
    @ReadOperation
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Request metrics
        metrics.put("totalRequests", metricsFilter.getTotalRequests());
        metrics.put("successfulRequests", metricsFilter.getSuccessfulRequests());
        metrics.put("failedRequests", metricsFilter.getFailedRequests());
        
        // Response time metrics
        metrics.put("avgResponseTimeMs", metricsFilter.getAverageResponseTime());
        metrics.put("maxResponseTimeMs", metricsFilter.getMaxResponseTimeMs());
        
        // Top endpoints
        metrics.put("topEndpoints", metricsFilter.getTopEndpoints(10));
        
        // Status code distribution
        metrics.put("statusCodes", metricsFilter.getStatusCodeCounts());
        
        return metrics;
    }
}

