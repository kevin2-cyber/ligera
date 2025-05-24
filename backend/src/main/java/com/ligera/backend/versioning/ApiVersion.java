package com.ligera.backend.versioning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing available API versions
 */
@Getter
@RequiredArgsConstructor
public enum ApiVersion {
    V1("v1", "v1"),
    V2("v2", "v2"),
    LATEST("latest", "v2"); // Always points to the latest version
    
    private final String name;
    private final String mappedVersion; // The actual version this value maps to
    
    /**
     * Convert a string version to enum
     * 
     * @param version string version (e.g., "v1", "v2", "latest")
     * @return the corresponding ApiVersion, or null if not found
     */
    public static ApiVersion fromString(String version) {
        if (version == null) {
            return null;
        }
        
        // Standardize version string
        String normalizedVersion = version.toLowerCase().trim();
        
        // Check for prefix addition
        if (normalizedVersion.matches("\\d+")) {
            normalizedVersion = "v" + normalizedVersion;
        }
        
        for (ApiVersion apiVersion : ApiVersion.values()) {
            if (apiVersion.name.equalsIgnoreCase(normalizedVersion)) {
                return apiVersion;
            }
        }
        
        return null;
    }
    
    /**
     * Get the default version if none is specified
     * 
     * @return the default API version
     */
    public static ApiVersion getDefault() {
        return V1;
    }
    
    /**
     * Check if a version is supported
     * 
     * @param version string version to check
     * @return true if version is supported
     */
    public static boolean isSupported(String version) {
        return fromString(version) != null;
    }
}

