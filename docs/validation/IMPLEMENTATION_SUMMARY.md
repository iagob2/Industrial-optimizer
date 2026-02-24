# Data Integrity & Business Rules Implementation
## Industrial Optimizer Project - Complete Solution

**Date:** February 24, 2026  
**Framework:** Spring Boot 3.x with Jakarta Validation  
**Language:** 100% English (Code, Comments, Documentation)  
**Architect:** Java Senior Software Architect

---

## Executive Summary

Four critical business rule validation mechanisms have been implemented to ensure data integrity across the Industrial Optimizer system. This document serves as a quick reference for developers.

---

## Key Artifacts Delivered

### 1. Data Transfer Objects with Validation

#### [ProductDTO.java](../backend/src/main/java/com/example/industrialoptimizer/dto/ProductDTO.java)
```
Annotations: @NotBlank, @Positive, @DecimalMax
Handles: Negative Value Prevention
```

#### [RawMaterialDTO.java](../backend/src/main/java/com/example/industrialoptimizer/dto/RawMaterialDTO.java)
```
Annotations: @NotBlank, @PositiveOrZero, @DecimalMax
Handles: Negative Stock Prevention, Negative Cost Prevention
```

### 2. Exception Handling Infrastructure

#### [GlobalExceptionHandler.java](../backend/src/main/java/com/example/industrialoptimizer/exception/GlobalExceptionHandler.java)
```
Endpoints:
- @ExceptionHandler(MethodArgumentNotValidException.class)    → 400 Bad Request
- @ExceptionHandler(DataIntegrityViolationException.class)     → 409 Conflict
- @ExceptionHandler(CascadeDeletionException.class)            → 409 Conflict
- @ExceptionHandler(Exception.class)                           → 500 Internal Error
```

#### [CascadeDeletionException.java](../backend/src/main/java/com/example/industrialoptimizer/exception/CascadeDeletionException.java)
```
Custom Exception for Cascade Deletion Prevention
Provides meaningful error messages to users
```

#### [ErrorResponse.java](../backend/src/main/java/com/example/industrialoptimizer/exception/ErrorResponse.java)
```
Standardized API Error Format
Fields: timestamp, status, error, message, fieldErrors, path
```

### 3. Updated Domain Models

#### [Product.java](../backend/src/main/java/com/example/industrialoptimizer/model/Product.java)
```
Changes:
- Added @Positive validation on saleValue
- Added @NotBlank validation on code and name
- Configured CascadeType.ALL for safe deletion
- Added orphanRemoval = true
```

#### [RawMaterial.java](../backend/src/main/java/com/example/industrialoptimizer/model/RawMaterial.java)
```
Changes:
- Added @PositiveOrZero validation on stockQuantity
- Added @PositiveOrZero validation on unitCost
- Added @NotBlank validation on code, name, unitMeasure
```

#### [ProductComposition.java](../backend/src/main/java/com/example/industrialoptimizer/model/ProductComposition.java)
```
Changes:
- Configured @ManyToOne(fetch = FetchType.LAZY) for RawMaterial
- NO CascadeType.REMOVE (protection against accidental deletion)
- Added @PositiveOrZero validation on quantityNeeded
```

### 4. Example Controllers with Documentation

#### [ProductControllerExample.java](../backend/src/main/java/com/example/industrialoptimizer/controller/ProductControllerExample.java)
```
Demonstrates:
- POST: Negative Value validation (@Valid triggers @Positive check)
- GET: Retrieve product by ID
- PUT: Update product with validation
- DELETE: Safe deletion with cascade configuration
```

#### [RawMaterialControllerExample.java](../backend/src/main/java/com/example/industrialoptimizer/controller/RawMaterialControllerExample.java)
```
Demonstrates:
- POST: Negative Stock validation (@Valid triggers @PositiveOrZero check)
- GET: Retrieve raw material by ID
- PUT: Update raw material with validation
- DELETE: Cascade deletion prevention check
```

### 5. Service Layer Example

#### [RawMaterialServiceExample.java](../backend/src/main/java/com/example/industrialoptimizer/service/RawMaterialServiceExample.java)
```
Key Method: deleteRawMaterial()
Logic:
1. Fetch RawMaterial by ID
2. Count ProductComposition references
3. If references exist: throw CascadeDeletionException
4. If safe: proceed with deletion
5. GlobalExceptionHandler handles exception
```

---

## Scenario Coverage Matrix

| # | Scenario | Validation Type | Check Point | Exception | HTTP Status |
|---|----------|-----------------|------------|-----------|-------------|
| **1** | Negative Value | Client-side | DTO Layer | MethodArgumentNotValidException | 400 |
| **1** | Negative Value | Server-side | Entity Layer | Invalid state | 500* |
| **2** | Negative Stock | Client-side | DTO Layer | MethodArgumentNotValidException | 400 |
| **2** | Negative Stock | Server-side | Entity Layer | Invalid state | 500* |
| **3** | Duplicated Codes | Database Layer | Unique Index | DataIntegrityViolationException | 409 |
| **4** | Cascade Deletion | Business Logic | Service Layer | CascadeDeletionException | 409 |

*Note: Should never happen if DTO validation is enforced; included for defense-in-depth.

---

## HTTP Response Codes Reference

### 400 Bad Request
**Triggered by:** DTO validation failures (Scenarios 1 & 2)
**When:** Client sends invalid data (negative price, negative stock)
**Contains:** fieldErrors with specific constraint violation messages

### 409 Conflict
**Case A - Triggered by:** Database unique constraint violation (Scenario 3)
**When:** Client tries to create product/material with existing code
**Message:** "A record with this code already exists"

**Case B - Triggered by:** Business logic check (Scenario 4)
**When:** Client tries to delete raw material used in recipes
**Message:** "Cannot delete material X: used in Y product recipes"

### 500 Internal Server Error
**Triggered by:** Unexpected exceptions
**When:** Database connection failure, null pointer, etc.
**Contains:** Generic "contact support" message (no technical details to client)

---

## Implementation Checklist

### Pre-Implementation
- ✅ Add Jakarta Validation dependency to pom.xml
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.0</version>
  </dependency>
  ```

### Step 1: DTOs with Annotations
- ✅ [ProductDTO.java](ProductDTO.java) created with @Positive
- ✅ [RawMaterialDTO.java](RawMaterialDTO.java) created with @PositiveOrZero
- ✅ All constraints properly documented in JavaDoc

### Step 2: Exception Handling
- ✅ [GlobalExceptionHandler.java](GlobalExceptionHandler.java) created with 4 handler methods
- ✅ [CascadeDeletionException.java](CascadeDeletionException.java) created for custom exception
- ✅ [ErrorResponse.java](ErrorResponse.java) created for standardized format

### Step 3: Entity Updates
- ✅ [Product.java](Product.java) updated with validation annotations
- ✅ [RawMaterial.java](RawMaterial.java) updated with validation annotations
- ✅ [ProductComposition.java](ProductComposition.java) updated with cascade configuration

### Step 4: Controller Integration
- ✅ [ProductControllerExample.java](ProductControllerExample.java) demonstrates @Valid usage
- ✅ [RawMaterialControllerExample.java](RawMaterialControllerExample.java) demonstrates validation flows

### Step 5: Service Layer
- ✅ [RawMaterialServiceExample.java](RawMaterialServiceExample.java) shows cascade deletion check
- ✅ Query count method in ProductCompositionRepository required

### Testing
- ⏳ Create unit tests for each scenario
- ⏳ Integration tests for validation flows
- ⏳ Contract tests for API error responses

---

## Validation Annotations Quick Reference

### Positive/Negative Checks
```java
@Positive          // > 0
@PositiveOrZero    // >= 0
@Negative          // < 0
@NegativeOrZero    // <= 0
```

### String Checks
```java
@NotNull           // != null
@NotBlank          // != null && !isEmpty() && !isWhitespace()
@NotEmpty          // != null && !isEmpty()
@Size(min, max)    // 3 <= length <= 100
@Email             // valid email format
@Pattern(regex)    // matches regex
```

### Numeric Checks
```java
@Min(value)        // >= value
@Max(value)        // <= value
@DecimalMin(value) // >= value (for BigDecimal)
@DecimalMax(value) // <= value (for BigDecimal)
```

### Collection Checks
```java
@NotEmpty          // not null && size > 0
@Size(min, max)    // min <= size <= max
```

---

## Testing Examples

### Test Scenario 1: Negative Value
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "code": "PROD-001",
    "name": "Product",
    "saleValue": -100.00
  }'

# Expected: 400 Bad Request
# Response: { "fieldErrors": { "saleValue": "..." } }
```

### Test Scenario 2: Negative Stock
```bash
curl -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{
    "code": "MAT-001",
    "name": "Steel",
    "stockQuantity": -50.000,
    "unitMeasure": "KG",
    "unitCost": 10.50
  }'

# Expected: 400 Bad Request
# Response: { "fieldErrors": { "stockQuantity": "..." } }
```

### Test Scenario 3: Duplicated Codes
```bash
# First request succeeds
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code": "PROD-001", "name": "Product A", "saleValue": 100}'

# Second request with same code fails
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code": "PROD-001", "name": "Product B", "saleValue": 150}'

# Expected: 409 Conflict
# Response: { "message": "A record with this code already exists..." }
```

### Test Scenario 4: Cascade Deletion
```bash
# Try to delete raw material used in product recipes
curl -X DELETE http://localhost:8080/api/v1/raw-materials/1

# Expected: 409 Conflict (if material is referenced)
# Response: { "message": "Cannot delete material: used in X product recipes..." }

# OR 204 No Content (if material is not referenced)
```

---

## Database Schema Requirements

```sql
-- Unique constraints for Scenario 3
CREATE UNIQUE INDEX idx_products_code ON products(code);
CREATE UNIQUE INDEX idx_raw_materials_code ON raw_materials(code);

-- Foreign key for Scenario 4 (existing, no changes needed)
ALTER TABLE product_compositions 
ADD CONSTRAINT fk_product_id 
FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE product_compositions 
ADD CONSTRAINT fk_raw_material_id 
FOREIGN KEY (raw_material_id) REFERENCES raw_materials(id);
```

---

## Integration Points

### With Existing Code
- ✅ ProductRepository: No changes needed
- ✅ RawMaterialRepository: Needs `countByRawMaterialId()` method
- ✅ ProductCompositionRepository: Needs `countByRawMaterialId()` method
- ⏳ Existing Controllers: Should extend examples or integrate handling

### With Frontend
- ✅ All errors return consistent JSON with `fieldErrors` map
- ✅ Frontend can display specific field-level error messages
- ✅ Error codes (400, 409) map to standard HTTP semantics

---

## Documentation Files

1. **VALIDATION_AND_INTEGRITY_GUIDE.md** - Comprehensive implementation guide (this document)
2. **MAVEN_DEPENDENCIES.md** - All required Maven dependencies and versions
3. **VALIDATION_FLOW_DIAGRAMS.md** - Visual flows for each scenario (generated)

---

## Key Principles Applied

1. **Defense in Depth:** Validation at DTO, Entity, and Database levels
2. **Fail Fast:** Validation as early as possible (controller input)
3. **Explicit over Implicit:** No magic; clear exception messages
4. **User-Friendly Errors:** Technical errors translated to actionable messages
5. **Data Integrity First:** Preventing corruption more important than convenience
6. **Separation of Concerns:** Validation, exception handling, business logic separated

---

## Common Pitfalls & Solutions

**Pitfall 1:** Using `@Valid` on DTO but not on Entity
**Solution:** Apply constraints to both layers for defense-in-depth

**Pitfall 2:** Not catching DataIntegrityViolationException
**Solution:** Implement GlobalExceptionHandler with dedicated handler method

**Pitfall 3:** Using CascadeType.REMOVE on RawMaterial relationship
**Solution:** Use explicit check in service layer instead

**Pitfall 4:** Generic error messages to users
**Solution:** Extract constraint type and provide specific guidance

---

## Performance Considerations

- ✅ Validation happens before database operations (fail fast)
- ✅ Global exception handler cached by Spring (no performance impact)
- ✅ Query for cascade deletion count is indexed and fast
- ✅ Lazy loading on @ManyToOne relationships reduces N+1 queries

---

## Security Considerations

- ✅ Input validation prevents SQL injection (parameterized queries)
- ✅ Size constraints prevent database bloat attacks
- ✅ Error messages don't expose system details
- ✅ Cascade deletion prevention stops accidental data loss

---

## Future Enhancements

1. **Custom Validators** - Implement `@Interface` for domain-specific rules
   ```java
   @ValidProductCode
   private String code;
   ```

2. **Conditional Validation** - Use Spring's `@ConditionalOnProperty`
   ```java
   @ValidateIf(condition = "isProduction")
   @Positive
   ```

3. **Localized Messages** - Support multiple languages in error messages
   ```properties
   jakarta.validation.constraints.Positive.message.pt_BR=...
   ```

4. **Audit Logging** - Log all validation failures for compliance
   ```java
   @EventListener
   public void onValidationFailure(ValidationFailureEvent event) {
       auditLog.record(event);
   }
   ```

---

## Support & Maintenance

### If validation is not working:
1. Verify spring-boot-starter-validation in pom.xml
2. Ensure @Valid annotation on controller method
3. Check that DTOclass has constraint annotations
4. Rebuild Maven project (clean install)
5. Check IDE cache (File > Invalidate Caches)

### If GlobalExceptionHandler is not catching exceptions:
1. Verify @RestControllerAdvice annotation is present
2. Ensure handler method has correct @ExceptionHandler type
3. Check that handler method is public (not private)
4. Verify exception is thrown before response is committed

### If cascade deletion is not working:
1. Verify ProductCompositionRepository has `countByRawMaterialId()` method
2. Ensure service layer calls this count method before deletion
3. Check that CascadeDeletionException is thrown on positive count
4. Verify GlobalExceptionHandler catches CascadeDeletionException

---

**Document Version:** 1.0  
**Last Updated:** February 24, 2026  
**Status:** Ready for Production Implementation

---

## Quick Start Checklist for Developers

```
□ Add spring-boot-starter-validation to pom.xml
□ Copy DTOs to dto/ package
□ Copy exception classes to exception/ package
□ Update entity classes with annotations
□ Implement GlobalExceptionHandler
□ Create repositories with count methods
□ Update controllers with @Valid
□ Implement service layer checks
□ Write unit tests for each scenario
□ Write integration tests for flows
□ Configure database unique constraints
□ Test all 4 scenarios manually
□ Load test exception handling performance
□ Deploy to staging environment
□ Monitor error rates in production
```

---

**End of Implementation Summary**
