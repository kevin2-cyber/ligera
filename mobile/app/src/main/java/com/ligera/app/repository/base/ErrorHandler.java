package com.ligera.app.repository.base;

/**
 * Interface for handling repository errors
 */
public interface ErrorHandler {
    
    /**
     * Error type enumeration
     */
    enum ErrorType {
        NETWORK,
        DATABASE,
        SERVER,
        AUTHENTICATION,
        AUTHORIZATION,
        VALIDATION,
        NOT_FOUND,
        TIMEOUT,
        RATE_LIMIT,
        UNKNOWN
    }
    
    /**
     * Handle an error
     *
     * @param error The error to handle
     * @param operationName Name of the operation that failed
     * @return True if the error was handled, false otherwise
     */
    boolean handleError(Throwable error, String operationName);
    
    /**
     * Get the error message for an error
     *
     * @param error The error
     * @return The error message
     */
    String getErrorMessage(Throwable error);
    
    /**
     * Get the error type for an error
     *
     * @param error The error
     * @return The error type
     */
    ErrorType getErrorType(Throwable error);
    
    /**
     * Check if the error is recoverable
     *
     * @param error The error
     * @return True if the error is recoverable, false otherwise
     */
    boolean isErrorRecoverable(Throwable error);
    
    /**
     * Get a recovery action for an error
     *
     * @param error The error
     * @return The recovery action or null if no recovery is possible
     */
    Runnable getRecoveryAction(Throwable error);
    
    /**
     * Log an error
     *
     * @param error The error to log
     * @param operationName Name of the operation that failed
     */
    void logError(Throwable error, String operationName);
}

