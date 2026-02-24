package com.example.industrialoptimizer.controller;

import com.example.industrialoptimizer.dto.RawMaterialDTO;
import com.example.industrialoptimizer.exception.CascadeDeletionException;
import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RawMaterialController: REST API endpoints for RawMaterial management.
 * 
 * Demonstrates:
 * 1. Negative Stock Validation: @Valid triggers RawMaterialDTO validation
 * 2. Cascade Deletion Prevention: Check references before delete
 * 3. Service layer throws CascadeDeletionException when needed
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/raw-materials")
public class RawMaterialControllerExample {

    private final RawMaterialService rawMaterialService;

    public RawMaterialControllerExample(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    /**
     * Create a new RawMaterial.
     * 
     * Scenario 2: Negative Stock Validation
     * =====================================
     * 
     * Example Request 1 (INVALID - Negative Stock):
     * POST /api/v1/raw-materials
     * {
     * "code": "MAT-001",
     * "name": "Steel",
     * "stockQuantity": -50.000,
     * "unitMeasure": "KG",
     * "unitCost": 10.50
     * }
     * 
     * Response (400 Bad Request):
     * {
     * "timestamp": "2026-02-24T10:30:45.123456",
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "Invalid input parameters",
     * "fieldErrors": {
     * "stockQuantity": "Stock quantity cannot be negative"
     * }
     * }
     * 
     * Example Request 2 (VALID - Zero Stock):
     * {
     * "code": "MAT-001",
     * "name": "Steel",
     * "stockQuantity": 0.000,
     * "unitMeasure": "KG",
     * "unitCost": 10.50
     * }
     * 
     * Response (201 Created):
     * {
     * "id": 1,
     * "code": "MAT-001",
     * "name": "Steel",
     * "stockQuantity": 0.000,
     * "unitMeasure": "KG",
     * "unitCost": 10.50
     * }
     * 
     * Example Request 3 (VALID - Positive Stock):
     * {
     * "code": "MAT-001",
     * "name": "Steel",
     * "stockQuantity": 1000.500,
     * "unitMeasure": "KG",
     * "unitCost": 10.50
     * }
     * 
     * Validation details:
     * - @PositiveOrZero on stockQuantity: Allows >= 0, rejects < 0
     * - @NotBlank on code and name
     * - @PositiveOrZero on unitCost: Allows free/donated materials
     * - Validation is triggered by @Valid annotation
     * - GlobalExceptionHandler catches MethodArgumentNotValidException
     */
    @PostMapping
    public ResponseEntity<RawMaterialDTO> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
        log.info("Creating raw material with code: {}", rawMaterialDTO.getCode());

        RawMaterial rawMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);

        RawMaterialDTO responseDTO = RawMaterialDTO.builder()
                .id(rawMaterial.getId())
                .code(rawMaterial.getCode())
                .name(rawMaterial.getName())
                .stockQuantity(rawMaterial.getStockQuantity())
                .unitMeasure(rawMaterial.getUnitMeasure())
                .unitCost(rawMaterial.getUnitCost())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Get all RawMaterials.
     */
    @GetMapping
    public ResponseEntity<List<RawMaterialDTO>> getAllRawMaterials() {
        log.info("Fetching all raw materials");

        List<RawMaterial> rawMaterials = rawMaterialService.getAllRawMaterials();

        List<RawMaterialDTO> responseDTOs = rawMaterials.stream()
                .map(m -> RawMaterialDTO.builder()
                        .id(m.getId())
                        .code(m.getCode())
                        .name(m.getName())
                        .stockQuantity(m.getStockQuantity())
                        .unitMeasure(m.getUnitMeasure())
                        .unitCost(m.getUnitCost())
                        .build())
                .toList();

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Delete a RawMaterial by ID.
     * 
     * Scenario 4: Cascade Deletion Prevention
     * =======================================
     * 
     * Example Request 1 (SUCCESS - No references):
     * DELETE /api/v1/raw-materials/5
     * 
     * Assuming RawMaterial id=5 is not referenced in any ProductComposition...
     * 
     * What happens:
     * 1. RawMaterialService.deleteRawMaterial(5) is called
     * 2. Service checks: SELECT COUNT(*) FROM product_compositions WHERE
     * raw_material_id = 5
     * 3. Result: 0 references
     * 4. Proceed with deletion: DELETE FROM raw_materials WHERE id = 5
     * 
     * Response (204 No Content):
     * (empty body)
     * 
     * Example Request 2 (FAILURE - Has references):
     * DELETE /api/v1/raw-materials/1
     * 
     * Assuming RawMaterial id=1 (Steel) is used in 3 ProductComposition records...
     * 
     * What happens:
     * 1. RawMaterialService.deleteRawMaterial(1) is called
     * 2. Service checks: SELECT COUNT(*) FROM product_compositions WHERE
     * raw_material_id = 1
     * 3. Result: 3 references found
     * 4. Service throws CascadeDeletionException with detailed message
     * 5. GlobalExceptionHandler.handleCascadeDeletionException() catches it
     * 
     * Response (409 Conflict):
     * {
     * "timestamp": "2026-02-24T10:32:15.789012",
     * "status": 409,
     * "error": "Business Rule Violation",
     * "message": "Cannot delete raw material MAT-001: it is used in 3 product
     * recipes.
     * Remove all product compositions first.",
     * "path": "/api/v1/raw-materials/1"
     * }
     * 
     * Why this approach?
     * ==================
     * Instead of using CascadeType.REMOVE, we:
     * 1. Explicitly check references before deletion
     * 2. Throw meaningful exception with context
     * 3. Inform user how to fix the problem
     * 4. Prevent accidental data loss
     * 
     * This ensures data integrity and provides better user feedback.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        log.info("Deleting raw material with id: {}", id);

        try {
            rawMaterialService.deleteRawMaterial(id);
            return ResponseEntity.noContent().build();
        } catch (CascadeDeletionException e) {
            log.warn("Cannot delete raw material: {}", e.getMessage());
            throw e; // GlobalExceptionHandler will catch this
        }
    }

    /**
     * Update a RawMaterial.
     * 
     * Scenario 2: Negative Stock Validation (on update)
     * ==================================================
     * 
     * Example Request (INVALID):
     * PUT /api/v1/raw-materials/1
     * {
     * "code": "MAT-001",
     * "name": "Steel",
     * "stockQuantity": -100.000,
     * "unitMeasure": "KG",
     * "unitCost": 10.50
     * }
     * 
     * Response (400 Bad Request):
     * {
     * "timestamp": "2026-02-24T10:32:45.111111",
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "Invalid input parameters",
     * "fieldErrors": {
     * "stockQuantity": "Stock quantity cannot be negative"
     * }
     * }
     * 
     * Same validation flow as POST request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> updateRawMaterial(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialDTO rawMaterialDTO) {

        log.info("Updating raw material with id: {}", id);

        RawMaterial rawMaterial = rawMaterialService.updateRawMaterial(id, rawMaterialDTO);

        RawMaterialDTO responseDTO = RawMaterialDTO.builder()
                .id(rawMaterial.getId())
                .code(rawMaterial.getCode())
                .name(rawMaterial.getName())
                .stockQuantity(rawMaterial.getStockQuantity())
                .unitMeasure(rawMaterial.getUnitMeasure())
                .unitCost(rawMaterial.getUnitCost())
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
