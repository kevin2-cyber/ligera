package com.ligera.backend.security.validation;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates password strength according to configurable security requirements
 */
@Component
@ConfigurationProperties(prefix = "app.security.password")
@Slf4j
@Getter
@Setter
public class PasswordValidator {

    // Configurable password requirements with defaults
    private int minLength = 8;
    private int minUppercase = 1;
    private int minLowercase = 1;
    private int minDigits = 1;
    private int minSpecialChars = 1;
    private boolean checkCommonPasswords = true;
    private String commonPasswordsPath = "security/common-passwords.txt";
    
    // Common patterns for password validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");
    
    // Cache of common passwords
    private Set<String> commonPasswords;
    
    /**
     * Initialize common passwords list if enabled
     */
    public void initializeCommonPasswords() {
        if (checkCommonPasswords && commonPasswords == null) {
            try {
                loadCommonPasswords();
            } catch (IOException e) {
                log.error("Failed to load common passwords list", e);
                // Fallback to a minimal set of extremely common passwords
                commonPasswords = new HashSet<>(List.of(
                        "password", "123456", "qwerty", "admin", "welcome", 
                        "password123", "abc123", "letmein", "monkey", "1234567890"
                ));
            }
        }
    }
    
    /**
     * Validates a password against the security requirements
     * 
     * @param password the password to validate
     * @return ValidationResult with validation details
     */
    public ValidationResult validate(String password) {
        // Initialize common passwords if needed
        if (checkCommonPasswords && commonPasswords == null) {
            try {
                initializeCommonPasswords();
            } catch (Exception e) {
                log.warn("Error initializing common passwords list", e);
            }
        }
        
        List<String> validationErrors = new ArrayList<>();
        
        // Check password length
        if (password == null || password.length() < minLength) {
            validationErrors.add("Password must be at least " + minLength + " characters long");
            
            // If password is too short, don't do other checks
            if (password == null || password.length() < 4) {
                return new ValidationResult(false, validationErrors);
            }
        }
        
        // Check for uppercase letters
        if (minUppercase > 0 && (!UPPERCASE_PATTERN.matcher(password).find() || 
                countMatches(password, UPPERCASE_PATTERN) < minUppercase)) {
            validationErrors.add("Password must contain at least " + minUppercase + " uppercase letter(s)");
        }
        
        // Check for lowercase letters
        if (minLowercase > 0 && (!LOWERCASE_PATTERN.matcher(password).find() || 
                countMatches(password, LOWERCASE_PATTERN) < minLowercase)) {
            validationErrors.add("Password must contain at least " + minLowercase + " lowercase letter(s)");
        }
        
        // Check for digits
        if (minDigits > 0 && (!DIGIT_PATTERN.matcher(password).find() || 
                countMatches(password, DIGIT_PATTERN) < minDigits)) {
            validationErrors.add("Password must contain at least " + minDigits + " digit(s)");
        }
        
        // Check for special characters
        if (minSpecialChars > 0 && (!SPECIAL_CHAR_PATTERN.matcher(password).find() || 
                countMatches(password, SPECIAL_CHAR_PATTERN) < minSpecialChars)) {
            validationErrors.add("Password must contain at least " + minSpecialChars + " special character(s)");
        }
        
        // Check if password is a common password
        if (checkCommonPasswords && commonPasswords != null && commonPasswords.contains(password.toLowerCase())) {
            validationErrors.add("Password is too common and easily guessable");
        }
        
        // Check for sequential patterns
        if (hasSequentialPattern(password)) {
            validationErrors.add("Password contains sequential patterns like '123' or 'abc'");
        }
        
        // Check for repeated characters
        if (hasRepeatedCharacters(password, 3)) {
            validationErrors.add("Password contains repeated characters (e.g., 'aaa')");
        }
        
        return new ValidationResult(validationErrors.isEmpty(), validationErrors);
    }
    
    /**
     * Count the number of matches for a pattern in a string
     */
    private int countMatches(String string, Pattern pattern) {
        var matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    
    /**
     * Check if a password has sequential patterns
     */
    private boolean hasSequentialPattern(String password) {
        // Common sequences to check
        String[] sequences = {
                "abcdefghijklmnopqrstuvwxyz",
                "0123456789",
                "qwertyuiop",
                "asdfghjkl",
                "zxcvbnm"
        };
        
        String lowerPass = password.toLowerCase();
        
        // Check for common sequences (forward and backward)
        for (String seq : sequences) {
            for (int i = 0; i < seq.length() - 2; i++) {
                String pattern = seq.substring(i, i + 3);
                if (lowerPass.contains(pattern)) {
                    return true;
                }
                
                // Check reverse pattern
                String reversePattern = new StringBuilder(pattern).reverse().toString();
                if (lowerPass.contains(reversePattern)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if a password has repeated characters
     */
    private boolean hasRepeatedCharacters(String password, int repeatCount) {
        if (password == null || password.length() < repeatCount) {
            return false;
        }
        
        for (int i = 0; i <= password.length() - repeatCount; i++) {
            char c = password.charAt(i);
            boolean repeated = true;
            
            for (int j = 1; j < repeatCount; j++) {
                if (password.charAt(i + j) != c) {
                    repeated = false;
                    break;
                }
            }
            
            if (repeated) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Load the list of common passwords from a file
     */
    private void loadCommonPasswords() throws IOException {
        commonPasswords = new HashSet<>();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(commonPasswordsPath)) {
            if (is == null) {
                log.warn("Common passwords file not found: {}", commonPasswordsPath);
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        commonPasswords.add(line.toLowerCase());
                    }
                }
            }
        }
        
        log.info("Loaded {} common passwords from {}", commonPasswords.size(), commonPasswordsPath);
    }
    
    /**
     * Result of password validation
     */
    public record ValidationResult(boolean valid, List<String> errors) {
        @Nonnull
        @Override
        public String toString() {
            if (valid) {
                return "Password validation passed";
            } else {
                return "Password validation failed: " + String.join(", ", errors);
            }
        }
    }
}

