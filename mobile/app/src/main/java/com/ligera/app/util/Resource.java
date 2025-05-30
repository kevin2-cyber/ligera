package com.ligera.app.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 */
public class Resource<T> {

    @NonNull
    private final Status status;

    @Nullable
    private final T data;

    @Nullable
    private final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * Creates a success resource with data
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /**
     * Creates an error resource with error message and optional data
     */
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    /**
     * Creates a loading resource with optional data
     */
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    /**
     * Status of the resource
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
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
}

