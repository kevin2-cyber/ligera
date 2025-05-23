package com.ligera.backend.config;

import com.ligera.backend.versioning.ApiVersionRequestMappingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Configuration for API versioning
 */
@Configuration
public class VersioningConfig implements WebMvcConfigurer {

    @Value("${app.api.versioning.path-prefix:/api}")
    private String apiPathPrefix;
    
    @Value("${app.api.versioning.header-name:X-API-Version}")
    private String versionHeaderName;
    
    @Value("${app.api.versioning.accept-header-prefix:application/vnd.ligera.}")
    private String acceptHeaderPrefix;
    
    /**
     * Create the custom request mapping handler for API versioning
     */
    @Bean
    public RequestMappingHandlerMapping apiVersionRequestMappingHandler() {
        ApiVersionRequestMappingHandler handler = new ApiVersionRequestMappingHandler(
                apiPathPrefix, versionHeaderName, acceptHeaderPrefix);
        handler.setOrder(0); // Give this handler higher precedence
        return handler;
    }
}

