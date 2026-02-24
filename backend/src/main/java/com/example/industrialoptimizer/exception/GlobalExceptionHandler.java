package com.example.industrialoptimizer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * GlobalExceptionHandler: Centralized exception handling for the application.
 * 
 * Handles:
 * 1. Validation Errors (Negative Value, Negative Stock, Required Fields)
 * 2. Duplicated Codes (DataIntegrityViolationException)
 * 3. Cascade Deletion Prevention (Custom exceptions)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Jakarta Validation errors.
     * 
     * Scenario 1: Negative Value
     * - When ProductDTO.saleValue = -100 or 0
     * - @Positive validation fails with user-friendly message
     * 
     * Scenario 2: Negative Stock
     * - When RawMaterialDTO.stockQuantity = -50
     * - @PositiveOrZero validation fails
     * 
     * Example response:
     * {
     * "timestamp": "2026-02-24T10:30:45.123456",
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "Invalid input parameters",
     * "fieldErrors": {
     * "saleValue": "Sale value must be greater than zero (R$ 0.00 is not allowed)",
     * "stockQuantity": "Stock quantity cannot be negative"
     * }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        log.warn("Validation exception occurred: {}", ex.getMessage());

        // Extract validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input parameters")
                .fieldErrors(fieldErrors)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Duplicated Codes scenario.
     * 
     * Scenario 3: Duplicated Codes
     * - When user tries to create Product with code = "PROD-001" (already exists)
     * - Database constraint violation (unique index on 'code' column)
     * - Spring throws DataIntegrityViolationException
     * 
     * Example response:
     * {
     * "timestamp": "2026-02-24T10:31:20.456789",
     * "status": 409,
     * "error": "Conflict",
     * "message": "A record with code 'PROD-001' already exists. Please use a
     * different code.",
     * "path": "/api/v1/products"
     * }
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        log.error("Data integrity violation: {}", ex.getMessage());

        String errorMessage = extractUniqueConstraintMessage(ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(errorMessage)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles missing required request parameters.
     * 
     * Example: GET /api/product-compositions (missing productId parameter)
     * 
     * Returns 400 Bad Request with clear message about what parameter is required.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            WebRequest request) {

        log.warn("Missing required parameter: {}", ex.getParameterName());

        String message = String.format(
                "Required parameter '%s' is missing. Please provide a valid value.",
                ex.getParameterName());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 
     * Scenario 4: Cascade Deletion
     * - When user tries to delete RawMaterial that is referenced in
     * ProductComposition
     * - Configured with @ManyToOne(fetch = FetchType.LAZY) on RawMaterial
     * - Without CascadeType.REMOVE, deletion is prevented by database constraint
     * 
     * Custom exception thrown by service layer.
     * 
     * Example response:
     * {
     * "timestamp": "2026-02-24T10:32:15.789012",
     * "status": 409,
     * "error": "Business Rule Violation",
     * "message": "Cannot delete raw material MAT-001: it is used in 3 product
     * recipes.
     * Remove all product compositions first.",
     * "path": "/api/v1/raw-materials/1"
     * }
     */
    @ExceptionHandler(CascadeDeletionException.class)
    public ResponseEntity<ErrorResponse> handleCascadeDeletionException(
            CascadeDeletionException ex,
            WebRequest request) {

        log.warn("Cascade deletion prevented: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Business Rule Violation")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Global exception handler for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please contact support.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Extracts user-friendly error message from DataIntegrityViolationException.
     * Identifies unique constraint violations and suggests alternatives.
     *
     * @param ex the DataIntegrityViolationException
     * @return user-friendly error message
     */
    private String extractUniqueConstraintMessage(DataIntegrityViolationException ex) {
        String message = ex.getMessage();

        if (message != null && message.toLowerCase().contains("unique")) {
            if (message.contains("code")) {
                return "A record with this code already exists. Please use a different code.";
            }
            if (message.contains("email")) {
                return "This email is already registered.";
            }
            return "A duplicate entry was detected. Please verify your data and try again.";
        }

        return "A database constraint was violated. Please verify your input and try again.";
    }
}
