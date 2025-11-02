package com.ligera.app.repository.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

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
        LOADING,
        OFFLINE
    }

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Throwable error;

    /**
     * Private constructor to enforce the use of factory methods
     *
     * @param status  Resource status
     * @param data    Resource data
     * @param message Message (for error resources)
     * @param error   Error throwable (for error resources)
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
     * @param <T>  Type of the data
     * @return A new successful resource
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    /**
     * Create an error resource with an error message and optional data
     *
     * @param msg  The error message
     * @param data Optional data
     * @param <T>  Type of the data
     * @return A new error resource
     */
    public static <T> Resource<T> error(@NonNull String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, null);
    }

    /**
     * Create an error resource with an error message, throwable, and optional data
     *
     * @param msg   The error message
     * @param error The error throwable
     * @param data  Optional data
     * @param <T>   Type of the data
     * @return A new error resource
     */
    public static <T> Resource<T> error(@NonNull String msg, @Nullable Throwable error, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, error);
    }

    /**
     * Creates a loading resource with optional data
     *
     * @param data The data
     * @param <T>  Type of the data
     * @return A new loading resource
     */
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    /**
     * Creates a loading resource
     *
     * @param <T>  Type of the data
     * @return A new loading resource
     */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, null);
    }

    /**
     * Creates an offline resource with optional data and a message.
     *
     * @param data Optional data
     * @param message A message describing the offline state
     * @param <T> Type of the data
     * @return A new offline resource
     */
    public static <T> Resource<T> offline(@Nullable T data, @NonNull String message) {
        return new Resource<>(Status.OFFLINE, data, message, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource<?> resource = (Resource<?>) o;
        return status == resource.status &&
                Objects.equals(data, resource.data) &&
                Objects.equals(message, resource.message) &&
                Objects.equals(error, resource.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, message, error);
    }

    @NonNull
    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", error=" + error +
                '}';
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isOffline() {
        return status == Status.OFFLINE;
    }
}
