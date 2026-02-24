package com.example.industrialoptimizer.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse: Standard error response format for all API exceptions.
 * 
 * Structure:
 * - timestamp: When the error occurred
 * - status: HTTP status code
 * - error: Error category
 * - message: User-friendly error message
 * - fieldErrors: Field-level validation errors (optional)
 * - path: API endpoint that generated the error
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;

    private Integer status;

    private String error;

    private String message;

    /**
     * Field-level validation errors (only included for validation exceptions).
     * 
     * Example:
     * {
     * "saleValue": "Sale value must be greater than zero",
     * "stockQuantity": "Stock quantity cannot be negative"
     * }
     */
    private Map<String, String> fieldErrors;

    private String path;
}
