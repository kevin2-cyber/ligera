package com.ligera.backend.exception;

import java.util.List;

/**
 * Exception thrown when a password does not meet strength requirements
 */
public class PasswordValidationException extends AuthException {

    private final List<String> validationErrors;

    /**
     * Create a new PasswordValidationException with validation errors
     * 
     * @param message the exception message
     * @param validationErrors list of specific validation errors
     */
    public PasswordValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    /**
     * Get the validation errors
     * 
     * @return list of validation error messages
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}

