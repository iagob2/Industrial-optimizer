package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.dto.RawMaterialDTO;
import com.example.industrialoptimizer.exception.CascadeDeletionException;
import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.repository.ProductCompositionRepository;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RawMaterialService: Business logic for RawMaterial management.
 * 
 * Critical Method: deleteRawMaterial()
 * ====================================
 * 
 * Scenario 4: Cascade Deletion Prevention
 * 
 * This service layer implements the explicit check for cascade deletion:
 * - BEFORE deleting a RawMaterial, check if it's referenced in
 * ProductComposition
 * - If referenced, throw CascadeDeletionException
 * - GlobalExceptionHandler catches it and returns 409 Conflict
 */
@Slf4j
@Service
public class RawMaterialServiceExample {

    private final RawMaterialRepository rawMaterialRepository;
    private final ProductCompositionRepository productCompositionRepository;

    public RawMaterialServiceExample(
            RawMaterialRepository rawMaterialRepository,
            ProductCompositionRepository productCompositionRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.productCompositionRepository = productCompositionRepository;
    }

    /**
     * Create a new RawMaterial.
     * - DTOs with @Valid trigger validation
     * - @PositiveOrZero on stockQuantity prevents negative values
     * 
     * Scenario 2: Negative Stock Validation
     * ======================================
     * 
     * If stockQuantity = -50:
     * 1. Controller receives RawMaterialDTO
     * 2. @Valid annotation triggers validation
     * 3. @PositiveOrZero on stockQuantity = -50 fails
     * 4. MethodArgumentNotValidException is thrown
     * 5. GlobalExceptionHandler.handleValidationException() handles it
     * 6. Returns 400 Bad Request with field error details
     * 
     * This method is never called with invalid data.
     */
    @Transactional
    public RawMaterial createRawMaterial(RawMaterialDTO rawMaterialDTO) {
        log.info("Creating raw material: {}", rawMaterialDTO.getCode());

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setCode(rawMaterialDTO.getCode());
        rawMaterial.setName(rawMaterialDTO.getName());
        rawMaterial.setStockQuantity(rawMaterialDTO.getStockQuantity());
        rawMaterial.setUnitMeasure(rawMaterialDTO.getUnitMeasure());
        rawMaterial.setUnitCost(rawMaterialDTO.getUnitCost());

        return rawMaterialRepository.save(rawMaterial);
    }

    /**
     * Get all RawMaterials.
     */
    public List<RawMaterial> getAllRawMaterials() {
        log.info("Fetching all raw materials");
        return rawMaterialRepository.findAll();
    }

    /**
     * Delete a RawMaterial with Cascade Deletion Prevention.
     * 
     * CRITICAL IMPLEMENTATION DETAILS
     * ===============================
     * 
     * Scenario 4: Cascade Deletion Prevention
     * 
     * Flow:
     * 1. Receive RawMaterial id to delete
     * 2. Query ProductComposition to find references
     * 3. If references exist, THROW CascadeDeletionException
     * 4. If no references, proceed with deletion
     * 
     * Why explicit check instead of CascadeType.REMOVE?
     * ================================================
     * 
     * Option A: Use CascadeType.REMOVE (BAD ❌)
     * --------
     * 
     * @ManyToOne
     *            private RawMaterial rawMaterial;
     * 
     *            Problem: Deleting RawMaterial would DELETE all ProductComposition
     *            records
     *            that reference it. But those ProductComposition records are
     *            RECIPES used
     *            by other Products! This would orphan product recipes and corrupt
     *            data integrity.
     * 
     *            Example:
     *            - Delete RawMaterial "Steel" (id=1)
     *            - CascadeType.REMOVE silently deletes all ProductComposition where
     *            raw_material_id=1
     *            - 5 products that use "Steel" suddenly have missing ingredients!
     *            - Database is corrupted, no error message to the user
     * 
     *            Option B: Explicit Check with Exception (GOOD ✓)
     *            --------
     *            What we do here:
     *            1. Check COUNT(*) FROM product_compositions WHERE raw_material_id
     *            = ?
     *            2. If count > 0, throw CascadeDeletionException with details
     *            3. User gets 409 Conflict with helpful message
     *            4. User must first remove ProductComposition entries
     *            5. Data integrity is preserved
     * 
     *            Example:
     *            - Attempt to delete RawMaterial "Steel" (id=1)
     *            - Service finds 5 ProductComposition references
     *            - Throws: "Cannot delete raw material STEEL: it is used in 5
     *            product recipes."
     *            - User understands the problem and fixes it
     *            - Data is safe
     * 
     * @param rawMaterialId the ID of the RawMaterial to delete
     * @throws CascadeDeletionException if the material is referenced in product
     *                                  recipes
     */
    @Transactional
    public void deleteRawMaterial(Long rawMaterialId) {
        log.info("Attempting to delete raw material with id: {}", rawMaterialId);

        // Step 1: Fetch the RawMaterial
        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + rawMaterialId));

        // Step 2: CHECK FOR CASCADE DELETION PREVENTION
        // Count how many ProductComposition records reference this RawMaterial
        long compositionCount = productCompositionRepository.countByRawMaterialId(rawMaterialId);

        // Step 3: If references exist, THROW exception
        if (compositionCount > 0) {
            log.warn("Cannot delete raw material {}: has {} product composition references",
                    rawMaterial.getCode(), compositionCount);

            String errorMessage = String.format(
                    "Cannot delete raw material %s: it is used in %d product recipe(s). " +
                            "Remove all product compositions first.",
                    rawMaterial.getCode(),
                    compositionCount);

            throw new CascadeDeletionException(errorMessage);
        }

        // Step 4: If no references, proceed with safe deletion
        log.info("Raw material {} has no references. Proceeding with deletion.", rawMaterial.getCode());
        rawMaterialRepository.delete(rawMaterial);
        log.info("Raw material {} deleted successfully.", rawMaterial.getCode());
    }

    /**
     * Update a RawMaterial.
     * - DTOs with @Valid trigger validation
     * - @PositiveOrZero on stockQuantity prevents negative values
     * 
     * Scenario 2: Negative Stock Validation (on update)
     * ==================================================
     * 
     * If stockQuantity = -50:
     * 1. Controller receives RawMaterialDTO
     * 2. @Valid annotation triggers validation
     * 3. @PositiveOrZero on stockQuantity = -50 fails
     * 4. MethodArgumentNotValidException is thrown
     * 5. GlobalExceptionHandler handles it, returns 400 Bad Request
     * 
     * This method is never called with invalid data.
     */
    @Transactional
    public RawMaterial updateRawMaterial(Long rawMaterialId, RawMaterialDTO rawMaterialDTO) {
        log.info("Updating raw material with id: {}", rawMaterialId);

        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + rawMaterialId));

        rawMaterial.setCode(rawMaterialDTO.getCode());
        rawMaterial.setName(rawMaterialDTO.getName());
        rawMaterial.setStockQuantity(rawMaterialDTO.getStockQuantity());
        rawMaterial.setUnitMeasure(rawMaterialDTO.getUnitMeasure());
        rawMaterial.setUnitCost(rawMaterialDTO.getUnitCost());

        return rawMaterialRepository.save(rawMaterial);
    }
}
