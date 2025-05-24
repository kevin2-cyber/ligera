package com.ligera.backend.exception;

/**
 * Exception thrown when a client has exceeded their rate limit
 */
public class RateLimitExceededException extends RuntimeException {

    /**
     * Create a new RateLimitExceededException with the default message
     */
    public RateLimitExceededException() {
        super("Rate limit exceeded. Please try again later.");
    }

    /**
     * Create a new RateLimitExceededException with a custom message
     * 
     * @param message the message to include in the exception
     */
    public RateLimitExceededException(String message) {
        super(message);
    }

    /**
     * Create a new RateLimitExceededException with a custom message and cause
     * 
     * @param message the message to include in the exception
     * @param cause the underlying cause of the exception
     */
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}

