# Industrial Optimizer: Data Integrity & Validation Implementation

**Date**: February 24, 2026  
**Architect**: Java Senior (Software Architecture)  
**Framework**: Spring Boot 3.x with Jakarta Validation

---

## Executive Summary

This document outlines the implementation of 4 critical data integrity and business rule enforcement mechanisms for the Industrial Optimizer project using Jakarta Validation and Spring Exception Handling.

---

## SCENARIO 1: Negative Value Validation

### Problem Statement
Users should NOT be able to register a Product with:
- Negative price (e.g., R$ -100.00)
- Zero price (e.g., R$ 0.00)

### Solution: Jakarta Validation @Positive Annotation

**DTO Implementation** (ProductDTO.java):
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    @NotBlank(message = "Product code cannot be blank")
    private String code;
    
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    
    /**
     * CRITICAL: @Positive validates saleValue > 0 (strictly greater than)
     * - Rejects: -100, 0, -0.01
     * - Accepts: 0.01, 99.99, 1000.00
     */
    @NotNull(message = "Sale value cannot be null")
    @Positive(message = "Sale value must be greater than zero (R$ 0.00 is not allowed)")
    @DecimalMax(value = "999999.99", message = "Sale value cannot exceed R$ 999,999.99")
    private BigDecimal saleValue;
}
```

**Entity Validation** (Product.java):
```java
@Entity
@Table(name = "products")
public class Product {
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Product code cannot be blank")
    private String code;
    
    @Column(nullable = false)
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    
    @Column(name = "sale_value", precision = 15, scale = 2)
    @NotNull(message = "Sale value cannot be null")
    @Positive(message = "Sale value must be greater than zero")
    private BigDecimal saleValue;
}
```

### Controller Usage:
```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
    // @Valid annotation triggers Jakarta Validation
    // If saleValue = -100 or 0: MethodArgumentNotValidException is thrown
    // GlobalExceptionHandler catches it and returns 400 with field error
    
    Product product = productService.createProduct(productDTO);
    return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
}
```

### Example Test Cases:

**Case 1A: Negative Price (INVALID)**
```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": -100.00
}

Response (400 Bad Request):
{
  "timestamp": "2026-02-24T10:30:45.123456",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "fieldErrors": {
    "saleValue": "Sale value must be greater than zero (R$ 0.00 is not allowed)"
  }
}
```

**Case 1B: Zero Price (INVALID)**
```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": 0.00
}

Response (400 Bad Request): Same as above
```

**Case 1C: Valid Price (VALID)**
```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": 99.99
}

Response (201 Created):
{
  "id": 1,
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": 99.99
}
```

### Validation Annotations Reference

| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@Positive` | Value > 0 (strictly greater) | Price must be positive |
| `@PositiveOrZero` | Value >= 0 | Stock quantity, cost |
| `@NotBlank` | String not null/empty | Code, name fields |
| `@NotNull` | Value not null | Required fields |
| `@DecimalMax` | Maximum value limit | Max price limit |

---

## SCENARIO 2: Negative Stock Validation

### Problem Statement
Users should NOT be able to register a RawMaterial with:
- Negative stock (e.g., -50 KG)
- Negative unit cost (e.g., -R$ 10.00)

Note: Zero stock IS allowed (empty warehouse shelf)

### Solution: Jakarta Validation @PositiveOrZero Annotation

**DTO Implementation** (RawMaterialDTO.java):
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialDTO {
    
    @NotBlank(message = "Raw material code cannot be blank")
    private String code;
    
    @NotBlank(message = "Raw material name cannot be blank")
    private String name;
    
    /**
     * CRITICAL: @PositiveOrZero validates stockQuantity >= 0
     * - Rejects: -50, -0.001
     * - Accepts: 0, 0.001, 1000.500
     */
    @NotNull(message = "Stock quantity cannot be null")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    @DecimalMax(value = "999999.999", message = "Stock quantity exceeds maximum limit")
    private BigDecimal stockQuantity;
    
    @NotBlank(message = "Unit measure cannot be blank")
    private String unitMeasure;
    
    /**
     * CRITICAL: @PositiveOrZero also allows free/donated materials (cost = 0)
     * - Rejects: -10
     * - Accepts: 0, 25.50
     */
    @NotNull(message = "Unit cost cannot be null")
    @PositiveOrZero(message = "Unit cost cannot be negative")
    @DecimalMax(value = "99999.99", message = "Unit cost exceeds maximum limit")
    private BigDecimal unitCost;
}
```

**Entity Validation** (RawMaterial.java):
```java
@Entity
@Table(name = "raw_materials")
public class RawMaterial {
    @Column(name = "stock_quantity", precision = 15, scale = 3)
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private BigDecimal stockQuantity;
    
    @Column(name = "unit_cost", precision = 15, scale = 2)
    @PositiveOrZero(message = "Unit cost cannot be negative")
    private BigDecimal unitCost;
}
```

### Controller Usage:
```java
@PostMapping
public ResponseEntity<RawMaterialDTO> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
    // @Valid annotation triggers Jakarta Validation
    // If stockQuantity < 0 or unitCost < 0: MethodArgumentNotValidException is thrown
    // GlobalExceptionHandler catches it and returns 400 with field error
    
    RawMaterial rawMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);
    return new ResponseEntity<>(rawMaterialDTO, HttpStatus.CREATED);
}
```

### Example Test Cases:

**Case 2A: Negative Stock (INVALID)**
```
POST /api/v1/raw-materials
{
  "code": "MAT-001",
  "name": "Steel",
  "stockQuantity": -50.000,
  "unitMeasure": "KG",
  "unitCost": 10.50
}

Response (400 Bad Request):
{
  "timestamp": "2026-02-24T10:30:45.123456",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "fieldErrors": {
    "stockQuantity": "Stock quantity cannot be negative"
  }
}
```

**Case 2B: Zero Stock (VALID - Empty Shelf)**
```
POST /api/v1/raw-materials
{
  "code": "MAT-001",
  "name": "Steel",
  "stockQuantity": 0.000,
  "unitMeasure": "KG",
  "unitCost": 10.50
}

Response (201 Created):
{
  "id": 1,
  "code": "MAT-001",
  "name": "Steel",
  "stockQuantity": 0.000,
  "unitMeasure": "KG",
  "unitCost": 10.50
}
```

**Case 2C: Negative Unit Cost (INVALID)**
```
POST /api/v1/raw-materials
{
  "code": "MAT-001",
  "name": "Steel",
  "stockQuantity": 100.000,
  "unitMeasure": "KG",
  "unitCost": -10.50
}

Response (400 Bad Request):
{
  "fieldErrors": {
    "unitCost": "Unit cost cannot be negative"
  }
}
```

**Case 2D: Free Material (VALID - Cost = 0)**
```
POST /api/v1/raw-materials
{
  "code": "MAT-002",
  "name": "Scrap Material",
  "stockQuantity": 50.000,
  "unitMeasure": "KG",
  "unitCost": 0.00
}

Response (201 Created): Success - donated materials are allowed
```

---

## SCENARIO 3: Duplicated Codes Detection

### Problem Statement
Users should NOT be able to register two Products or RawMaterials with the same code:
- Product with code "PROD-001" already exists
- Cannot create another "PROD-001"

### Solution: Unique Database Constraint + GlobalExceptionHandler

**Entity Configuration** (Product.java & RawMaterial.java):
```java
@Entity
@Table(name = "products")
public class Product {
    @Column(unique = true, nullable = false)
    // Database unique constraint on 'code' column
    private String code;
}

@Entity
@Table(name = "raw_materials")
public class RawMaterial {
    @Column(unique = true, nullable = false)
    // Database unique constraint on 'code' column
    private String code;
}
```

**Database Schema:**
```sql
CREATE UNIQUE INDEX idx_products_code ON products(code);
CREATE UNIQUE INDEX idx_raw_materials_code ON raw_materials(code);
```

**GlobalExceptionHandler** (GlobalExceptionHandler.java):
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles DataIntegrityViolationException when unique constraints are violated.
     * 
     * Flow:
     * 1. User tries to create Product with existing code
     * 2. ProductRepository.save() attempts INSERT
     * 3. Database unique constraint violation occurs
     * 4. JDBC throws constraint violation exception
     * 5. Spring wraps it in DataIntegrityViolationException
     * 6. This handler catches it and returns friendly error
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
     * Extracts user-friendly error message from database constraint violation.
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
```

### Example Test Cases:

**Case 3A: First Creation (VALID)**
```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": 100.00
}

Response (201 Created):
{
  "id": 1,
  "code": "PROD-001",
  "name": "Product A",
  "saleValue": 100.00
}
```

**Case 3B: Duplicate Code (INVALID)**
```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product B",  // Different name
  "saleValue": 150.00
}

Database Flow:
1. INSERT INTO products (code, name, sale_value) VALUES ('PROD-001', 'Product B', 150.00)
2. Database unique constraint violation on 'code' column
3. Spring catches: DataIntegrityViolationException
4. GlobalExceptionHandler.handleDataIntegrityViolation() processes it

Response (409 Conflict):
{
  "timestamp": "2026-02-24T10:31:20.456789",
  "status": 409,
  "error": "Conflict",
  "message": "A record with this code already exists. Please use a different code.",
  "path": "/api/v1/products"
}
```

**Case 3C: Different Code (VALID)**
```
POST /api/v1/products
{
  "code": "PROD-002",  // Different code
  "name": "Product B",
  "saleValue": 150.00
}

Response (201 Created): Success - new code is unique
```

### Validation Exception Flow Diagram:
```
User Request: POST /api/v1/products { code: "PROD-001", ... }
    ↓
Controller.createProduct(@Valid ProductDTO)
    ↓
@Valid Annotation ✓ (passes: code not blank, saleValue positive)
    ↓
ProductService.createProduct(ProductDTO)
    ↓
ProductRepository.save(Product)
    ↓
JDBC INSERT statement executes
    ↓
Database Unique Constraint Violation ✗
    ↓
Spring catches & wraps: DataIntegrityViolationException
    ↓
GlobalExceptionHandler.handleDataIntegrityViolation()
    ↓
Returns: 409 Conflict with friendly message
```

---

## SCENARIO 4: Cascade Deletion Prevention

### Problem Statement
Users should NOT be able to delete a RawMaterial if it's used in product recipes:
- RawMaterial "Steel" is used in 5 ProductComposition entries
- Deleting it would orphan 5 product recipes
- Must prevent deletion with meaningful error

### Architectural Decision: Explicit Check vs CascadeType.REMOVE

#### ❌ BAD APPROACH: CascadeType.REMOVE
```java
@ManyToOne(cascade = CascadeType.REMOVE)
private RawMaterial rawMaterial;

// Result: Silently deletes all ProductComposition records!
// Consequence: 5 products lose their ingredients, data corrupted, no error to user
```

#### ✅ GOOD APPROACH: Explicit Check with Exception
```java
@ManyToOne(fetch = FetchType.LAZY)
// NO CascadeType.REMOVE
private RawMaterial rawMaterial;

// Service layer checks references before deletion
// If references exist: throw CascadeDeletionException
// User gets 409 Conflict with helpful message
```

**Entity Configuration** (ProductComposition.java):
```java
@Entity
@Table(name = "product_compositions")
public class ProductComposition {

    @EmbeddedId
    private ProductCompositionKey id;

    /**
     * Relationship to Product WITH cascade delete (safe).
     * When Product is deleted, ProductComposition entries are also deleted.
     * This is SAFE because ProductComposition exists only to link Product to RawMaterial.
     */
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Relationship to RawMaterial WITHOUT cascade delete (protected).
     * CRITICAL: NO CascadeType.REMOVE
     * 
     * Service layer explicitly checks if RawMaterial is referenced
     * before allowing deletion.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @Column(name = "quantity_needed", precision = 15, scale = 3)
    private BigDecimal quantityNeeded;
}
```

**Custom Exception** (CascadeDeletionException.java):
```java
/**
 * CascadeDeletionException: Thrown when attempting to delete an entity
 * that is referenced by other entities.
 */
public class CascadeDeletionException extends RuntimeException {
    public CascadeDeletionException(String message) {
        super(message);
    }

    public CascadeDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**GlobalExceptionHandler** (GlobalExceptionHandler.java):
```java
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
```

**Service Layer** (RawMaterialServiceExample.java):
```java
@Service
@Transactional
public class RawMaterialServiceExample {

    private final RawMaterialRepository rawMaterialRepository;
    private final ProductCompositionRepository productCompositionRepository;

    /**
     * Delete a RawMaterial with explicit cascade deletion prevention.
     * 
     * Step 1: Fetch the RawMaterial
     * Step 2: Query ProductComposition for references
     * Step 3: If references found, throw CascadeDeletionException
     * Step 4: GlobalExceptionHandler catches and returns 409
     * Step 5: If no references, proceed with safe deletion
     */
    public void deleteRawMaterial(Long rawMaterialId) {
        log.info("Attempting to delete raw material: {}", rawMaterialId);

        // Step 1: Fetch the RawMaterial
        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found"));

        // Step 2: Count ProductComposition references
        long compositionCount = productCompositionRepository.countByRawMaterialId(rawMaterialId);

        // Step 3: If references exist, THROW exception
        if (compositionCount > 0) {
            String errorMessage = String.format(
                    "Cannot delete raw material %s: it is used in %d product recipe(s). " +
                    "Remove all product compositions first.",
                    rawMaterial.getCode(),
                    compositionCount
            );
            throw new CascadeDeletionException(errorMessage);
        }

        // Step 4: If no references, proceed with deletion
        rawMaterialRepository.delete(rawMaterial);
        log.info("Raw material {} deleted successfully.", rawMaterial.getCode());
    }
}
```

### Example Test Cases:

**Case 4A: Deletion Allowed (No References)**
```
DELETE /api/v1/raw-materials/5

Assuming: RawMaterial id=5 has NO ProductComposition references

Response (204 No Content):
(empty body - successful deletion)
```

**Case 4B: Deletion Blocked (Has References)**
```
DELETE /api/v1/raw-materials/1

Assuming: RawMaterial id=1 (Steel) is used in 3 ProductComposition records

Service Flow:
1. RawMaterialService.deleteRawMaterial(1)
2. Query: SELECT COUNT(*) FROM product_compositions WHERE raw_material_id = 1
3. Result: 3 references found
4. Throws: CascadeDeletionException with message
5. GlobalExceptionHandler catches and formats response

Response (409 Conflict):
{
  "timestamp": "2026-02-24T10:32:15.789012",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Cannot delete raw material STEEL: it is used in 3 product recipe(s). 
              Remove all product compositions first.",
  "path": "/api/v1/raw-materials/1"
}
```

### Database Query Implementation:

**ProductCompositionRepository.java:**
```java
@Repository
public interface ProductCompositionRepository extends JpaRepository<ProductComposition, ProductCompositionKey> {
    
    /**
     * Count ProductComposition records referencing a specific RawMaterial.
     * Used to prevent cascade deletion of materials still in use.
     */
    long countByRawMaterialId(Long rawMaterialId);
}
```

---

## Error Response Structure

**ErrorResponse.java:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    // When the error occurred
    private LocalDateTime timestamp;
    
    // HTTP status code (400, 409, 500, etc.)
    private Integer status;
    
    // Error category ("Validation Failed", "Conflict", "Business Rule Violation")
    private String error;
    
    // User-friendly error message
    private String message;
    
    // Field-level validation errors (only for validation exceptions)
    private Map<String, String> fieldErrors;
    
    // API endpoint that generated the error
    private String path;
}
```

### Example Error Responses:

**Validation Error (400 Bad Request):**
```json
{
  "timestamp": "2026-02-24T10:30:45.123456",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "fieldErrors": {
    "saleValue": "Sale value must be greater than zero",
    "stockQuantity": "Stock quantity cannot be negative"
  },
  "path": "/api/v1/products"
}
```

**Duplicate Code Error (409 Conflict):**
```json
{
  "timestamp": "2026-02-24T10:31:20.456789",
  "status": 409,
  "error": "Conflict",
  "message": "A record with this code already exists. Please use a different code.",
  "path": "/api/v1/products"
}
```

**Cascade Deletion Error (409 Conflict):**
```json
{
  "timestamp": "2026-02-24T10:32:15.789012",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Cannot delete raw material STEEL: it is used in 3 product recipe(s). Remove all product compositions first.",
  "path": "/api/v1/raw-materials/1"
}
```

---

## Summary Table

| Scenario | Validation Type | Annotation | Status Code | Handler |
|----------|-----------------|-----------|-------------|---------|
| 1. Negative Value | Client-side (DTO) | @Positive | 400 | GlobalExceptionHandler.handleValidationException() |
| 2. Negative Stock | Client-side (DTO) | @PositiveOrZero | 400 | GlobalExceptionHandler.handleValidationException() |
| 3. Duplicated Codes | Server-side (DB) | Unique Index | 409 | GlobalExceptionHandler.handleDataIntegrityViolation() |
| 4. Cascade Deletion | Business Logic | Custom Exception | 409 | GlobalExceptionHandler.handleCascadeDeletionException() |

---

## Classes Created

1. **DTOs with Validation:**
   - `ProductDTO.java` - @Positive on saleValue
   - `RawMaterialDTO.java` - @PositiveOrZero on stockQuantity and unitCost

2. **Exception Handling:**
   - `GlobalExceptionHandler.java` - Centralized exception handling
   - `CascadeDeletionException.java` - Custom business rule exception
   - `ErrorResponse.java` - Standardized error response format

3. **Updated Entities:**
   - `Product.java` - Added validation annotations and cascade configuration
   - `RawMaterial.java` - Added validation annotations
   - `ProductComposition.java` - Configured cascade behavior

4. **Example Controllers & Services:**
   - `ProductControllerExample.java` - Demonstrated all scenarios
   - `RawMaterialControllerExample.java` - Demonstrated all scenarios
   - `RawMaterialServiceExample.java` - Explicit cascade deletion check

---

## Testing Recommendations

```bash
# Test Scenario 1: Negative Value
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Test","saleValue":-100}'

# Test Scenario 2: Negative Stock
curl -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{"code":"MAT-001","name":"Steel","stockQuantity":-50,"unitMeasure":"KG","unitCost":10.50}'

# Test Scenario 3: Duplicated Codes
# First request succeeds, second fails
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Product A","saleValue":100}'

# Same code, should fail with 409
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Product B","saleValue":150}'

# Test Scenario 4: Cascade Deletion
curl -X DELETE http://localhost:8080/api/v1/raw-materials/1
# Returns 409 if used in products, 204 if safe to delete
```

---

## Architecture Principles Applied

1. **Defense in Depth:** Multiple validation layers (DTO, Entity, Database)
2. **Fail Fast:** Validation as early as possible (controller input)
3. **Explicit over Implicit:** No magical cascade deletion, explicit checks
4. **User-Friendly Errors:** Clear messages explaining what went wrong and how to fix it
5. **Separation of Concerns:** Validation, exception handling, and business logic separated
6. **Data Integrity First:** Preventing corruption more important than convenience

---

**End of Document**
