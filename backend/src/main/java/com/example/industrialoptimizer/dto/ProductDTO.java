package com.example.industrialoptimizer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ProductDTO: Data Transfer Object for Product entity with comprehensive
 * validation.
 * Handles: Negative Value, Duplicated Codes, and required field validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    /**
     * Product unique code (PROD-001, PROD-002, etc.)
     * Cannot be null, must not be blank.
     * - Negative Value (Scenario 1): Code uniqueness prevents duplicates
     */
    @NotBlank(message = "Product code cannot be blank")
    @Size(min = 3, max = 50, message = "Product code must be between 3 and 50 characters")
    private String code;

    /**
     * Product descriptive name.
     * Cannot be null, must not be blank.
     */
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    /**
     * Sale value of the product in Brazilian Reais (R$).
     * 
     * CRITICAL VALIDATION: Must be greater than zero.
     * - @Positive: Prevents R$ 0.00 and negative values
     * 
     * Negative Value (Scenario 1):
     * - If saleValue = -100: @Positive validation fails
     * - If saleValue = 0: @Positive validation fails
     * - If saleValue = 99.99: Valid ✓
     */
    @NotNull(message = "Sale value cannot be null")
    @Positive(message = "Sale value must be greater than zero (R$ 0.00 is not allowed)")
    @DecimalMax(value = "999999.99", message = "Sale value cannot exceed R$ 999,999.99")
    private BigDecimal saleValue;

    /**
     * Read-only field: Generated response only.
     */
    private Long id;
}
