package com.ligera.app.repository.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 * This is a immutable wrapper around data that is being loaded from various sources.
 *
 * @param <T> Type of the resource data
 */
public class Resource<T> {
    /**
     * Status of the resource
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }
    
    @NonNull
    public final Status status;
    
    @Nullable
    public final T data;
    
    @Nullable
    public final String message;
    
    @Nullable
    private final Throwable error;
    
    /**
     * Private constructor to enforce the use of factory methods
     *
     * @param status Resource status
     * @param data Resource data
     * @param message Message (for error resources)
     * @param error Error throwable (for error resources)
     */
    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.error = error;
    }
    
    //-------------------------------------------------------------------------------------------
    // Factory methods for creating resources
    //-------------------------------------------------------------------------------------------
    
    /**
     * Create a successful resource with data
     *
     * @param data The data
     * @param <T> Type of the data
     * @return A new successful resource
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }
    
    /**
     * Create an error resource with an error message and optional data
     *
     * @param msg The error message
     * @param data Optional data
     * @param <T> Type of the data
     * @return A new error resource
     */
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, null);
    }
    
    /**
     * Create an error resource with an error message, throwable, and optional data
     *
     * @param msg The error message
     * @param error The error throwable
     * @param data Optional data
     * @param <T> Type of the data
     * @return A new error resource
     */
    public static <T> Resource<T> error(String msg, Throwable error, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, error);
    }
    
    /**
     * Create a loading resource with optional data
     *
     * @param data Optional data
     * @param <T> Type of the data
     * @return A new loading resource

package com.ligera.app.repository.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T> Type of the resource data
 */
public class Resource<T> {
    @NonNull
    public final Status status;
    
    @Nullable
    public final T data;
    
    @Nullable
    public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * Creates a successful resource with data
     *
     * @param data The data
     * @param <T> Type of the data
     * @return A new successful resource
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /**
     * Creates an error resource with an error message and optional data
     *
     * @param msg The error message
     * @param data Optional data
     * @param <T> Type of the data
     * @return A new error resource
     */
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    /**
     * Creates a loading resource with optional data
     *
     * @param data Optional data
     * @param <T> Type of the data
     * @return A new loading resource
     */
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    /**
     * Resource status
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) return false;
        if (data != null ? !data.equals(resource.data) : resource.data != null) return false;
        return message != null ? message.equals(resource.message) : resource.message == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

