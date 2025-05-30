package com.ligera.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HTTP response compression
 */
@Configuration
public class CompressionConfig {

    @Value("${server.compression.enabled:true}")
    private boolean compressionEnabled;
    
    @Value("${server.compression.min-response-size:2048}")
    private int minResponseSize;
    
    @Value("${server.compression.mime-types:application/json,application/xml,text/html,text/plain,text/css,text/javascript,application/javascript}")
    private String[] mimeTypes;
    
    @Value("${server.compression.excluded-user-agents:}")
    private String[] excludedUserAgents;

    /**
     * Configure response compression
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> compressionCustomizer() {
        return factory -> {
            Compression compression = new Compression();
            compression.setEnabled(compressionEnabled);
            compression.setMinResponseSize(minResponseSize);
            compression.setMimeTypes(mimeTypes);
            compression.setExcludedUserAgents(excludedUserAgents);
            factory.setCompression(compression);
        };
    }
}

