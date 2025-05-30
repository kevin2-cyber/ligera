package com.ligera.backend.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for CORS settings loaded from application properties
 */
@Configuration
@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
@Slf4j
public class CorsConfig {
    
    // Properties bound from application-{profile}.yml
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private String exposedHeaders;
    private Boolean allowCredentials;
    private Long maxAge;
    
    private final Environment environment;
    
    public CorsConfig(Environment environment) {
        this.environment = environment;
    }
    
    /**
     * Configure CORS using properties from application-{profile}.yml
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins (comma-separated list or "*")
        if (allowedOrigins != null) {
            if (allowedOrigins.equals("*")) {
                configuration.setAllowedOrigins(List.of("*"));
            } else {
                configuration.setAllowedOriginPatterns(
                        Arrays.asList(allowedOrigins.split(","))
                );
            }
        }
        
        // Parse allowed methods
        if (allowedMethods != null) {
            configuration.setAllowedMethods(
                    Arrays.asList(allowedMethods.split(","))
            );
        }
        
        // Parse allowed headers
        if (allowedHeaders != null) {
            configuration.setAllowedHeaders(
                    Arrays.asList(allowedHeaders.split(","))
            );
        }
        
        // Parse exposed headers
        if (exposedHeaders != null) {
            configuration.setExposedHeaders(
                    Arrays.asList(exposedHeaders.split(","))
            );
        }
        
        // Set allow credentials
        if (allowCredentials != null) {
            configuration.setAllowCredentials(allowCredentials);
        }
        
        // Set max age
        if (maxAge != null) {
            configuration.setMaxAge(maxAge);
        }
        
        // Log active CORS configuration based on profile
        logCorsConfig();
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Log CORS configuration on startup
     */
    private void logCorsConfig() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        if ("prod".equals(profile)) {
            log.info("CORS Configuration: Production mode with restricted origins: {}", allowedOrigins);
        } else {
            log.info("CORS Configuration: Development mode with allowed origins: {}", allowedOrigins);
        }
    }
}

