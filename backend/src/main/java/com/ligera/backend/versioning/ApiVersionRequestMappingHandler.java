package com.ligera.backend.versioning;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Custom request mapping handler that supports API versioning
 */
@Slf4j
public class ApiVersionRequestMappingHandler extends RequestMappingHandlerMapping {

    private final String apiPathPrefix;
    private final String versionHeaderName;
    private final String acceptHeaderPrefix;
    
    public ApiVersionRequestMappingHandler(String apiPathPrefix, String versionHeaderName, String acceptHeaderPrefix) {
        this.apiPathPrefix = apiPathPrefix;
        this.versionHeaderName = versionHeaderName;
        this.acceptHeaderPrefix = acceptHeaderPrefix;
    }
    
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        // Get the basic RequestMappingInfo from the standard logic
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo == null) {
            return null;
        }
        
        // Look for our custom annotation
        ApiVersionRequestMapping versionMapping = AnnotationUtils.findAnnotation(method, ApiVersionRequestMapping.class);
        if (versionMapping == null) {
            versionMapping = AnnotationUtils.findAnnotation(handlerType, ApiVersionRequestMapping.class);
        }
        
        // If no version annotation, return the standard mapping
        if (versionMapping == null) {
            return mappingInfo;
        }
        
        // Get the version from the annotation
        ApiVersion apiVersion = versionMapping.version();
        
        // Create version-specific request condition
        RequestCondition<?> versionCondition = createVersionCondition(apiVersion);
        
        // Combine the standard mapping with our version condition
        if (versionCondition != null) {
            mappingInfo = mappingInfo.combine(RequestMappingInfo
                    .paths(apiPathPrefix + "/" + apiVersion.getName())
                    .headers(versionHeaderName + "=" + apiVersion.getName())
                    .produces(acceptHeaderPrefix + apiVersion.getName() + "+json")
                    .build());
        }
        
        return mappingInfo;
    }
    
    /**
     * Create a version-specific request condition
     */
    private RequestCondition<?> createVersionCondition(ApiVersion apiVersion) {
        return new ApiVersionRequestCondition(apiVersion, versionHeaderName, acceptHeaderPrefix);
    }
    
    /**
     * Custom request condition for API versioning
     */
    private static class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {
        
        private final ApiVersion apiVersion;
        private final String versionHeaderName;
        private final String acceptHeaderPrefix;
        
        public ApiVersionRequestCondition(ApiVersion apiVersion, String versionHeaderName, String acceptHeaderPrefix) {
            this.apiVersion = apiVersion;
            this.versionHeaderName = versionHeaderName;
            this.acceptHeaderPrefix = acceptHeaderPrefix;
        }
        
        @Override
        public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
            // In case of combining, use the later version
            return new ApiVersionRequestCondition(
                    other.apiVersion, versionHeaderName, acceptHeaderPrefix);
        }

        @Override
        public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
            // Check if the request matches our version via path, header, or accept header
            ApiVersion requestVersion = getRequestedApiVersion(request);
            
            // If no version specified in request, use default
            if (requestVersion == null) {
                requestVersion = ApiVersion.getDefault();
            }
            
            // Return this condition if it matches the requested version
            return apiVersion == requestVersion ? this : null;
        }

        @Override
        public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
            // More specific versions have higher precedence
            return other.apiVersion.ordinal() - this.apiVersion.ordinal();
        }
        
        /**
         * Extract the requested API version from the request
         */
        private ApiVersion getRequestedApiVersion(HttpServletRequest request) {
            // Try to get version from path
            String path = request.getRequestURI();
            if (path.contains("/v")) {
                int index = path.indexOf("/v");
                if (index >= 0 && index + 2 < path.length()) {
                    String versionPart = path.substring(index + 1);
                    // Extract just the version part (v1, v2, etc.)
                    if (versionPart.contains("/")) {
                        versionPart = versionPart.substring(0, versionPart.indexOf("/"));
                    }
                    ApiVersion version = ApiVersion.fromString(versionPart);
                    if (version != null) {
                        return version;
                    }
                }
            }
            
            // Try to get version from header
            String versionHeader = request.getHeader(versionHeaderName);
            if (versionHeader != null) {
                ApiVersion version = ApiVersion.fromString(versionHeader);
                if (version != null) {
                    return version;
                }
            }
            
            // Try to get version from Accept header
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null && acceptHeader.contains(acceptHeaderPrefix)) {
                int startIndex = acceptHeader.indexOf(acceptHeaderPrefix) + acceptHeaderPrefix.length();
                int endIndex = acceptHeader.indexOf("+", startIndex);
                if (endIndex > startIndex) {
                    String versionPart = acceptHeader.substring(startIndex, endIndex);
                    ApiVersion version = ApiVersion.fromString(versionPart);
                    if (version != null) {
                        return version;
                    }
                }
            }
            
            return null;
        }
    }
}

