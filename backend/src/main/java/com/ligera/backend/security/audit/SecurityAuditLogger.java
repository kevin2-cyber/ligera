package com.ligera.backend.security.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Component for logging security events for audit purposes
 */
@Component
@Slf4j
public class SecurityAuditLogger {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Log a successful authentication event
     * 
     * @param username the authenticated user
     * @param request the HTTP request
     */
    public void logSuccessfulAuthentication(String username, HttpServletRequest request) {
        log.info(buildLogMessage(
                "AUTHENTICATION_SUCCESS",
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                "User authenticated successfully"
        ));
    }
    
    /**
     * Log a failed authentication event
     * 
     * @param username the attempted username
     * @param request the HTTP request
     * @param reason the reason for failure
     */
    public void logFailedAuthentication(String username, HttpServletRequest request, String reason) {
        log.warn(buildLogMessage(
                "AUTHENTICATION_FAILURE",
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                "Authentication failed: " + reason
        ));
    }
    
    /**
     * Log an access denied event
     * 
     * @param username the authenticated user who was denied access
     * @param request the HTTP request
     * @param resource the resource being accessed
     */
    public void logAccessDenied(String username, HttpServletRequest request, String resource) {
        log.warn(buildLogMessage(
                "ACCESS_DENIED",
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                "Access denied to resource: " + resource
        ));
    }
    
    /**
     * Log a password change event
     * 
     * @param username the user whose password was changed
     * @param request the HTTP request
     * @param success whether the password change was successful
     */
    public void logPasswordChange(String username, HttpServletRequest request, boolean success) {
        String eventType = success ? "PASSWORD_CHANGE_SUCCESS" : "PASSWORD_CHANGE_FAILURE";
        String message = success ? "Password changed successfully" : "Password change failed";
        
        log.info(buildLogMessage(
                eventType,
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                message
        ));
    }
    
    /**
     * Log an account lockout event
     * 
     * @param username the user whose account was locked
     * @param request the HTTP request
     * @param reason the reason for the lockout
     */
    public void logAccountLockout(String username, HttpServletRequest request, String reason) {
        log.warn(buildLogMessage(
                "ACCOUNT_LOCKOUT",
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                "Account locked: " + reason
        ));
    }
    
    /**
     * Log a suspicious activity event
     * 
     * @param username the user associated with the suspicious activity
     * @param request the HTTP request
     * @param activity description of the suspicious activity
     */
    public void logSuspiciousActivity(String username, HttpServletRequest request, String activity) {
        log.warn(buildLogMessage(
                "SUSPICIOUS_ACTIVITY",
                username,
                request.getRemoteAddr(),
                request.getRequestURI(),
                "Suspicious activity detected: " + activity
        ));
    }
    
    /**
     * Build a standardized log message for security events
     */
    private String buildLogMessage(String eventType, String username, String ipAddress, 
                                 String resourcePath, String message) {
        return String.format(
                "[SECURITY_AUDIT] [%s] [%s] [User: %s] [IP: %s] [Resource: %s] [Message: %s]",
                LocalDateTime.now().format(TIMESTAMP_FORMAT),
                eventType,
                username != null ? username : "anonymous",
                ipAddress,
                resourcePath,
                message
        );
    }
}

