package com.example.industrialoptimizer.controller;

import com.example.industrialoptimizer.dto.ProductDTO;
import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController: REST API endpoints for Product management.
 * 
 * Demonstrates:
 * 1. Negative Value Validation: @Valid triggers ProductDTO validation
 * 2. Duplicated Codes: GlobalExceptionHandler catches
 * DataIntegrityViolationException
 * 3. Cascade Deletion: DELETE endpoint with proper error handling
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerExample {

    private final ProductService productService;

    public ProductControllerExample(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new Product.
     * 
     * Scenario 1: Negative Value Validation
     * ======================================
     * 
     * Example Request 1 (INVALID - Negative Price):
     * POST /api/v1/products
     * {
     * "code": "PROD-001",
     * "name": "Product A",
     * "saleValue": -100.00
     * }
     * 
     * Response (400 Bad Request):
     * {
     * "timestamp": "2026-02-24T10:30:45.123456",
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "Invalid input parameters",
     * "fieldErrors": {
     * "saleValue": "Sale value must be greater than zero (R$ 0.00 is not allowed)"
     * }
     * }
     * 
     * Example Request 2 (INVALID - Zero Price):
     * {
     * "code": "PROD-001",
     * "name": "Product A",
     * "saleValue": 0.00
     * }
     * 
     * Response (400 Bad Request) - Same validation error
     * 
     * Example Request 3 (VALID):
     * {
     * "code": "PROD-001",
     * "name": "Product A",
     * "saleValue": 99.99
     * }
     * 
     * Response (201 Created):
     * {
     * "id": 1,
     * "code": "PROD-001",
     * "name": "Product A",
     * "saleValue": 99.99
     * }
     * 
     * @Valid annotation triggers Jakarta Validation
     *        - @NotBlank validates code and name
     *        - @Positive validates saleValue > 0
     *        - If validation fails, MethodArgumentNotValidException is thrown
     *        - GlobalExceptionHandler.handleValidationException() catches it
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        log.info("Creating product with code: {}", productDTO.getCode());

        Product product = productService.createProduct(productDTO);

        ProductDTO responseDTO = ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .saleValue(product.getSaleValue())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Create a new Product.
     * 
     * Scenario 3: Duplicated Codes
     * =============================
     * 
     * Example Request (INVALID - Code already exists):
     * POST /api/v1/products
     * {
     * "code": "PROD-001",
     * "name": "Product B",
     * "saleValue": 150.00
     * }
     * 
     * Assuming PROD-001 already exists in the database...
     * 
     * What happens:
     * 1. @Valid passes (all @Valid constraints satisfied)
     * 2. productService.createProduct(productDTO) is called
     * 3. ProductRepository.save() tries to insert
     * 4. Database constraint violation: unique index on 'code' column
     * 5. Spring wraps the JDBC error in DataIntegrityViolationException
     * 6. GlobalExceptionHandler.handleDataIntegrityViolation() catches it
     * 
     * Response (409 Conflict):
     * {
     * "timestamp": "2026-02-24T10:31:20.456789",
     * "status": 409,
     * "error": "Conflict",
     * "message": "A record with code 'PROD-001' already exists. Please use a
     * different code.",
     * "path": "/api/v1/products"
     * }
     * 
     * Error handling flow:
     * POST /api/v1/products
     * -> ProductController.createProduct()
     * -> ProductService.createProduct()
     * -> ProductRepository.save()
     * -> Database throws constraint violation
     * -> Spring catches and wraps in DataIntegrityViolationException
     * -> GlobalExceptionHandler.handleDataIntegrityViolation() handles it
     * -> Returns 409 Conflict with friendly message
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);

        Product product = productService.getProductById(id);

        ProductDTO responseDTO = ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .saleValue(product.getSaleValue())
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Get all Products.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("Fetching all products");

        List<Product> products = productService.getAllProducts();

        List<ProductDTO> responseDTOs = products.stream()
                .map(p -> ProductDTO.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .name(p.getName())
                        .saleValue(p.getSaleValue())
                        .build())
                .toList();

        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * Delete a Product by ID.
     * 
     * Scenario 4: Cascade Deletion (Product side)
     * ============================================
     * 
     * Example Request:
     * DELETE /api/v1/products/1
     * 
     * What happens:
     * 1. ProductService.deleteProduct(1) is called
     * 2. Product with id=1 is fetched
     * 3. Product has cascade configuration: cascade = CascadeType.ALL,
     * orphanRemoval = true
     * 4. When Product is deleted, all ProductComposition entries are also deleted
     * 5. RawMaterial records are NOT deleted (separate lifecycle)
     * 
     * Response (204 No Content):
     * (empty body)
     * 
     * NOTE: This is SAFE because ProductComposition exists ONLY to link
     * Product to RawMaterial. RawMaterial can be reused in other products.
     * 
     * Database operations:
     * - DELETE FROM product_compositions WHERE product_id = 1
     * - DELETE FROM products WHERE id = 1
     * - RawMaterial records remain untouched
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Update a Product.
     * 
     * Scenario 1: Negative Value Validation (on update)
     * ==================================================
     * 
     * Example Request (INVALID):
     * PUT /api/v1/products/1
     * {
     * "code": "PROD-001",
     * "name": "Updated Product",
     * "saleValue": -50.00
     * }
     * 
     * Response (400 Bad Request):
     * {
     * "timestamp": "2026-02-24T10:32:10.654321",
     * "status": 400,
     * "error": "Validation Failed",
     * "message": "Invalid input parameters",
     * "fieldErrors": {
     * "saleValue": "Sale value must be greater than zero (R$ 0.00 is not allowed)"
     * }
     * }
     * 
     * Same validation flow as POST request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {

        log.info("Updating product with id: {}", id);

        Product product = productService.updateProduct(id, productDTO);

        ProductDTO responseDTO = ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .saleValue(product.getSaleValue())
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
