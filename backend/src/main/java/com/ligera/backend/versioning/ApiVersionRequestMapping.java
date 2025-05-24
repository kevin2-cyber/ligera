package com.ligera.backend.versioning;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Custom annotation for API version-based request mapping
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
public @interface ApiVersionRequestMapping {
    /**
     * API version for this mapping
     */
    ApiVersion version() default ApiVersion.V1;
    
    /**
     * Path mapping for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};
    
    /**
     * Path mapping for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};
    
    /**
     * HTTP methods supported by this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    RequestMethod[] method() default {};
    
    /**
     * Parameters required for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] params() default {};
    
    /**
     * Headers required for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] headers() default {};
    
    /**
     * Supported content types for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] consumes() default {};
    
    /**
     * Produced content types for this endpoint
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] produces() default {};
}

