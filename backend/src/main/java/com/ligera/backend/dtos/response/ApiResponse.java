package com.ligera.backend.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard API response envelope for all API responses
 * @param <T> type of the data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String status;
    private int code;
    private String message;
    private T data;
    private Map<String, String> errors;
    private Meta meta;

    /**
     * Success response with data
     * 
     * @param data response data
     * @param message success message
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Success response with data
     * 
     * @param data response data
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    /**
     * Success response with no data
     * 
     * @param message success message
     * @return API response
     */
    public static ApiResponse<Void> success(String message) {
        return success(null, message);
    }

    /**
     * Created response with data
     * 
     * @param data response data
     * @param message success message
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Created response with data
     * 
     * @param data response data
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<T> created(T data) {
        return created(data, "Resource created successfully");
    }

    /**
     * Error response
     * 
     * @param status HTTP status
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return ApiResponse.<Void>builder()
                .status("error")
                .code(status.value())
                .message(message)
                .build();
    }

    /**
     * Error response with validation errors
     * 
     * @param status HTTP status
     * @param message error message
     * @param errors validation errors
     * @return API response
     */
    public static ApiResponse<Void> error(HttpStatus status, String message, Map<String, String> errors) {
        return ApiResponse.<Void>builder()
                .status("error")
                .code(status.value())
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Bad request error response
     * 
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Not found error response
     * 
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Unauthorized error response
     * 
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * Forbidden error response
     * 
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    /**
     * Server error response
     * 
     * @param message error message
     * @return API response
     */
    public static ApiResponse<Void> serverError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Paginated response
     * 
     * @param data paginated data
     * @param message success message
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<List<T>> paginated(Page<T> data, String message) {
        return ApiResponse.<List<T>>builder()
                .status("success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data.getContent())
                .meta(Meta.fromPage(data))
                .build();
    }

    /**
     * Paginated response
     * 
     * @param data paginated data
     * @param <T> type of data
     * @return API response
     */
    public static <T> ApiResponse<List<T>> paginated(Page<T> data) {
        return paginated(data, "Data retrieved successfully");
    }

    /**
     * Convert to ResponseEntity
     * 
     * @return ResponseEntity with appropriate status code
     */
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(code).body(this);
    }

    /**
     * Create a ResponseEntity with given HTTP status
     * 
     * @param status HTTP status
     * @return ResponseEntity with appropriate status code
     */
    public ResponseEntity<ApiResponse<T>> toResponseEntity(HttpStatus status) {
        return ResponseEntity.status(status).body(this);
    }

    /**
     * Metadata for paginated responses
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean empty;

        /**
         * Create Meta from a Page object
         * 
         * @param page the Page object
         * @return Meta object
         */
        public static Meta fromPage(Page<?> page) {
            return Meta.builder()
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .first(page.isFirst())
                    .last(page.isLast())
                    .empty(page.isEmpty())
                    .build();
        }
    }
}

