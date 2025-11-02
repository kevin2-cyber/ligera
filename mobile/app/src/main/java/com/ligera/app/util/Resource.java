package com.ligera.app.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Throwable error;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    /**
     * Creates a success resource with data
     */
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    /**
     * Creates an error resource with error message and optional data
     */
    public static <T> Resource<T> error(@NonNull String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, null);
    }

    /**
     * Creates an error resource with an error message, throwable, and optional data
     */
    public static <T> Resource<T> error(@NonNull String msg, @Nullable Throwable error, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, error);
    }

    /**
     * Creates a loading resource with optional data
     */
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    /**
     * Creates a loading resource
     */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, null);
    }

    /**
     * Creates an offline resource with optional data and a message.
     */
    public static <T> Resource<T> offline(@Nullable T data, @NonNull String message) {
        return new Resource<>(Status.OFFLINE, data, message, null);
    }

    /**
     * Status of the resource
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING,
        OFFLINE
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
