package com.ligera.backend.controller;

import com.ligera.backend.dtos.response.ApiResponse;
import com.ligera.backend.filter.MetricsFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for application metrics and monitoring
 */
@RestController
@RequestMapping("/admin/metrics")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Administrative operations for monitoring and control")
@com.ligera.backend.versioning.ApiVersionRequestMapping(version = com.ligera.backend.versioning.ApiVersion.V1)
public class MetricsController {

    private final MetricsFilter metricsFilter;

    /**
     * Get system metrics including memory usage, uptime, etc.
     */
    @GetMapping
    @Operation(
        summary = "Get system metrics",
        description = "Retrieves system metrics including memory usage, uptime, and more",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM memory metrics
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        Map<String, Object> memoryMetrics = new HashMap<>();
        memoryMetrics.put("heapMemoryUsed", bytesToMB(heapMemory.getUsed()));
        memoryMetrics.put("heapMemoryMax", bytesToMB(heapMemory.getMax()));
        memoryMetrics.put("heapMemoryCommitted", bytesToMB(heapMemory.getCommitted()));
        memoryMetrics.put("nonHeapMemoryUsed", bytesToMB(nonHeapMemory.getUsed()));
        metrics.put("memory", memoryMetrics);
        
        // JVM uptime
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtimeBean.getUptime();
        Duration uptime = Duration.ofMillis(uptimeMillis);
        
        Map<String, Object> runtimeMetrics = new HashMap<>();
        runtimeMetrics.put("uptimeDays", uptime.toDays());
        runtimeMetrics.put("uptimeHours", uptime.toHoursPart());
        runtimeMetrics.put("uptimeMinutes", uptime.toMinutesPart());
        runtimeMetrics.put("uptimeSeconds", uptime.toSecondsPart());
        runtimeMetrics.put("jvmName", runtimeBean.getVmName());
        runtimeMetrics.put("jvmVersion", runtimeBean.getVmVersion());
        metrics.put("runtime", runtimeMetrics);
        
        // Available processors
        metrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        return ApiResponse.success(metrics, "System metrics retrieved successfully");
    }
    
    /**
     * Reset all metrics counters
     */
    @PostMapping("/reset")
    @Operation(
        summary = "Reset metrics",
        description = "Resets all metrics counters to zero",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> resetMetrics() {
        metricsFilter.resetMetrics();
        return ApiResponse.success("Metrics reset successfully");
    }
    
    /**
     * Convert bytes to MB for more readable output
     */
    private double bytesToMB(long bytes) {
        return (double) bytes / (1024 * 1024);
    }
}

