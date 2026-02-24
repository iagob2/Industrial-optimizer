package com.example.industrialoptimizer.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * RawMaterialDTO: Data Transfer Object for RawMaterial entity with
 * comprehensive validation.
 * Handles: Negative Stock, Negative Unit Cost, and required field validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialDTO {

    /**
     * Raw material unique code (MAT-001, MAT-002, etc.)
     * Cannot be null, must not be blank.
     */
    @NotBlank(message = "Raw material code cannot be blank")
    @Size(min = 3, max = 50, message = "Raw material code must be between 3 and 50 characters")
    private String code;

    /**
     * Raw material descriptive name.
     * Cannot be null, must not be blank.
     */
    @NotBlank(message = "Raw material name cannot be blank")
    @Size(min = 3, max = 100, message = "Raw material name must be between 3 and 100 characters")
    private String name;

    /**
     * Current stock quantity in warehouse.
     * 
     * CRITICAL VALIDATION: Must be greater than or equal to zero.
     * - @PositiveOrZero: Prevents negative stock quantities
     * 
     * Negative Stock (Scenario 2):
     * - If stockQuantity = -50: @PositiveOrZero validation fails
     * - If stockQuantity = 0: Valid (empty stock) ✓
     * - If stockQuantity = 100: Valid ✓
     */
    @NotNull(message = "Stock quantity cannot be null")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    @DecimalMax(value = "999999.999", message = "Stock quantity exceeds maximum limit")
    private BigDecimal stockQuantity;

    /**
     * Unit of measure (KG, L, UNIT, etc.)
     * Cannot be null, must not be blank.
     */
    @NotBlank(message = "Unit measure cannot be blank")
    @Size(min = 2, max = 20, message = "Unit measure must be between 2 and 20 characters")
    private String unitMeasure;

    /**
     * Unit cost in Brazilian Reais (R$).
     * Must be positive or zero (cost cannot be negative).
     * 
     * CRITICAL VALIDATION: Must be greater than or equal to zero.
     * - @PositiveOrZero: Prevents negative unit costs
     * 
     * If unitCost = -10: @PositiveOrZero validation fails
     * If unitCost = 0: Valid (donated/free material) ✓
     * If unitCost = 25.50: Valid ✓
     */
    @NotNull(message = "Unit cost cannot be null")
    @PositiveOrZero(message = "Unit cost cannot be negative")
    @DecimalMax(value = "99999.99", message = "Unit cost exceeds maximum limit")
    private BigDecimal unitCost;

    /**
     * Read-only field: Generated response only.
     */
    private Long id;
}
