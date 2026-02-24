# 4 Cenários Obrigatórios: Resumo de Implementação
## Industrial Optimizer - Data Integrity & Business Rules

---

## ✅ CENÁRIO 1: Negative Value (Valor Negativo)

### Problema
Usuário tenta cadastrar produto com preço **R$ -100,00** ou **R$ 0,00**

### Solução
**Anotação Jakarta Validation:** `@Positive`

### Implementação

**ProductDTO.java:**
```java
@Data
@Builder
public class ProductDTO {
    @NotBlank(message = "Product code cannot be blank")
    private String code;
    
    @NotNull(message = "Sale value cannot be null")
    @Positive(message = "Sale value must be greater than zero")
    @DecimalMax(value = "999999.99")
    private BigDecimal saleValue;
}
```

**Product.java (Entity):**
```java
@Entity
@Table(name = "products")
public class Product {
    @Column(unique = true, nullable = false)
    @NotBlank
    private String code;
    
    @Column(name = "sale_value", precision = 15, scale = 2)
    @NotNull
    @Positive
    private BigDecimal saleValue;
}
```

**ProductController.java:**
```java
@PostMapping
public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
    // @Valid triggers validation
    // If saleValue = -100 or 0 → MethodArgumentNotValidException
    // GlobalExceptionHandler catches it → 400 Bad Request
    
    Product product = productService.createProduct(productDTO);
    return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
}
```

### Fluxo de Erro

```
POST /api/v1/products
{
  "code": "PROD-001",
  "name": "Product",
  "saleValue": -100.00
}

↓ @Valid fails on @Positive constraint
↓ MethodArgumentNotValidException thrown
↓ GlobalExceptionHandler.handleValidationException()
↓ 400 Bad Request

{
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "fieldErrors": {
    "saleValue": "Sale value must be greater than zero"
  }
}
```

### Casos Testados
- ✅ saleValue = -100 → **400** (Fail)
- ✅ saleValue = 0 → **400** (Fail)
- ✅ saleValue = 99.99 → **201** (Success)

---

## ✅ CENÁRIO 2: Negative Stock (Estoque Negativo)

### Problema
Usuário tenta cadastrar matéria-prima com estoque **-50 KG**

### Solução
**Anotação Jakarta Validation:** `@PositiveOrZero`

### Implementação

**RawMaterialDTO.java:**
```java
@Data
@Builder
public class RawMaterialDTO {
    @NotBlank(message = "Raw material code cannot be blank")
    private String code;
    
    @NotNull(message = "Stock quantity cannot be null")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    @DecimalMax(value = "999999.999")
    private BigDecimal stockQuantity;
    
    @NotNull(message = "Unit cost cannot be null")
    @PositiveOrZero(message = "Unit cost cannot be negative")
    @DecimalMax(value = "99999.99")
    private BigDecimal unitCost;
}
```

**RawMaterial.java (Entity):**
```java
@Entity
@Table(name = "raw_materials")
public class RawMaterial {
    @Column(name = "stock_quantity", precision = 15, scale = 3)
    @NotNull
    @PositiveOrZero
    private BigDecimal stockQuantity;
    
    @Column(name = "unit_cost", precision = 15, scale = 2)
    @NotNull
    @PositiveOrZero
    private BigDecimal unitCost;
}
```

**RawMaterialController.java:**
```java
@PostMapping
public ResponseEntity<RawMaterialDTO> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
    // @Valid triggers validation
    // If stockQuantity < 0 → MethodArgumentNotValidException
    // GlobalExceptionHandler catches it → 400 Bad Request
    
    RawMaterial rawMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);
    return new ResponseEntity<>(rawMaterialDTO, HttpStatus.CREATED);
}
```

### Fluxo de Erro

```
POST /api/v1/raw-materials
{
  "code": "MAT-001",
  "name": "Steel",
  "stockQuantity": -50.000,
  "unitMeasure": "KG",
  "unitCost": 10.50
}

↓ @Valid fails on @PositiveOrZero constraint
↓ MethodArgumentNotValidException thrown
↓ GlobalExceptionHandler.handleValidationException()
↓ 400 Bad Request

{
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "stockQuantity": "Stock quantity cannot be negative"
  }
}
```

### Casos Testados
- ✅ stockQuantity = -50 → **400** (Fail)
- ✅ stockQuantity = 0 → **201** (Success - estoque vazio permitido)
- ✅ stockQuantity = 1000.5 → **201** (Success)
- ✅ unitCost = -10 → **400** (Fail)
- ✅ unitCost = 0 → **201** (Success - material doado)

---

## ✅ CENÁRIO 3: Duplicated Codes (Códigos Duplicados)

### Problema
Usuário tenta criar segundo produto com código **PROD-001** (já existe)

### Solução
**Banco de Dados:** Unique Index + **GlobalExceptionHandler** captura `DataIntegrityViolationException`

### Implementação

**Product.java (Entity):**
```java
@Entity
@Table(name = "products")
public class Product {
    @Column(unique = true, nullable = false)
    private String code;
}
```

**Database Schema:**
```sql
CREATE UNIQUE INDEX idx_products_code ON products(code);
```

**GlobalExceptionHandler.java:**
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
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
    
    private String extractUniqueConstraintMessage(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message != null && message.toLowerCase().contains("unique")) {
            if (message.contains("code")) {
                return "A record with this code already exists. Please use a different code.";
            }
        }
        return "A database constraint was violated.";
    }
}
```

### Fluxo de Erro

```
Primeira requisição (Success):
POST /api/v1/products { code: "PROD-001", ... }
↓ INSERT INTO products VALUES ('PROD-001', ...)
↓ 201 Created

Segunda requisição com mesmo código (Failure):
POST /api/v1/products { code: "PROD-001", ... }
↓ INSERT INTO products VALUES ('PROD-001', ...)
↓ Database: UNIQUE constraint violation
↓ JDBC Exception thrown
↓ Spring wraps em DataIntegrityViolationException
↓ GlobalExceptionHandler.handleDataIntegrityViolation()
↓ 409 Conflict

{
  "status": 409,
  "error": "Conflict",
  "message": "A record with this code already exists. Please use a different code.",
  "path": "/api/v1/products"
}
```

### Casos Testados
- ✅ Primeiro cadastro: PROD-001 → **201** (Success)
- ✅ Segundo cadastro: PROD-001 → **409** (Conflict)
- ✅ Terceiro cadastro: PROD-002 → **201** (Success - código diferente)

---

## ✅ CENÁRIO 4: Cascade Deletion (Proteção contra deleção em cascata)

### Problema
Usuário tenta deletar matéria-prima "Aço" que está presente em 5 receitas de produtos

### Solução
**Service Layer:** Verificação explícita antes de deletar + **CascadeDeletionException**

### Implementação

**ProductComposition.java (Entity):**
```java
@Entity
@Table(name = "product_compositions")
public class ProductComposition {
    
    // Cascade safe: Product deletion removes this composition
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    // Cascade protected: NO CascadeType.REMOVE
    // RawMaterial cannot be deleted if referenced here
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;
}
```

**CascadeDeletionException.java:**
```java
public class CascadeDeletionException extends RuntimeException {
    public CascadeDeletionException(String message) {
        super(message);
    }
}
```

**GlobalExceptionHandler.java:**
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

**RawMaterialService.java:**
```java
@Service
@Transactional
public class RawMaterialService {
    
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductCompositionRepository productCompositionRepository;
    
    public void deleteRawMaterial(Long rawMaterialId) {
        log.info("Attempting to delete raw material: {}", rawMaterialId);
        
        // Step 1: Fetch the material
        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        
        // Step 2: CHECK FOR REFERENCES (Cascade Deletion Prevention)
        long compositionCount = productCompositionRepository
                .countByRawMaterialId(rawMaterialId);
        
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
        
        // Step 4: If safe, proceed with deletion
        rawMaterialRepository.delete(rawMaterial);
        log.info("Raw material {} deleted successfully.", rawMaterial.getCode());
    }
}
```

**ProductCompositionRepository.java:**
```java
@Repository
public interface ProductCompositionRepository 
        extends JpaRepository<ProductComposition, ProductCompositionKey> {
    
    long countByRawMaterialId(Long rawMaterialId);
}
```

### Fluxo de Erro

**Caso 1: Deleção permitida (sem referências)**
```
DELETE /api/v1/raw-materials/5

RawMaterial id=5 sem ProductComposition:
↓ countByRawMaterialId(5) = 0
↓ Safe to delete
↓ DELETE FROM raw_materials WHERE id = 5
↓ 204 No Content
```

**Caso 2: Deleção bloqueada (com referências)**
```
DELETE /api/v1/raw-materials/1

RawMaterial id=1 (Aço) em 3 ProductComposition:
↓ countByRawMaterialId(1) = 3
↓ throw CascadeDeletionException
↓ GlobalExceptionHandler.handleCascadeDeletionException()
↓ 409 Conflict

{
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Cannot delete raw material STEEL: it is used in 3 product recipe(s). 
              Remove all product compositions first.",
  "path": "/api/v1/raw-materials/1"
}
```

### Casos Testados
- ✅ Deletar material SEM referências → **204** (No Content)
- ✅ Deletar material COM referências → **409** (Conflict)

---

## Resumo Executivo dos 4 Cenários

| # | Cenário | Anotação | Tipo Validação | Status Code | Handler |
|---|---------|----------|-----------------|------------|---------|
| 1 | Negative Value | @Positive | DTO Input | 400 | handleValidationException |
| 2 | Negative Stock | @PositiveOrZero | DTO Input | 400 | handleValidationException |
| 3 | Duplicated Codes | Unique Index | Database | 409 | handleDataIntegrityViolation |
| 4 | Cascade Deletion | Custom Logic | Service | 409 | handleCascadeDeletionException |

---

## Classes Criadas (Todos em Inglês 100%)

1. ✅ **ProductDTO.java** - @Positive on saleValue
2. ✅ **RawMaterialDTO.java** - @PositiveOrZero on stockQuantity & unitCost
3. ✅ **GlobalExceptionHandler.java** - 4 handler methods
4. ✅ **CascadeDeletionException.java** - Custom exception
5. ✅ **ErrorResponse.java** - Standard error format
6. ✅ **Product.java** (Updated) - Entity annotations
7. ✅ **RawMaterial.java** (Updated) - Entity annotations
8. ✅ **ProductComposition.java** (Updated) - Cascade configuration
9. ✅ **ProductControllerExample.java** - Demonstrates all scenarios
10. ✅ **RawMaterialControllerExample.java** - Demonstrates all scenarios
11. ✅ **RawMaterialServiceExample.java** - Cascade deletion logic

---

## Documentação de Referência

- 📄 **VALIDATION_AND_INTEGRITY_GUIDE.md** - Guia completo de implementação
- 📄 **MAVEN_DEPENDENCIES.md** - Dependências do Maven
- 📄 **IMPLEMENTATION_SUMMARY.md** - Resumo executivo
- 🎨 **Validation Flow Diagrams** - Diagramas visuais dos 4 cenários

---

## Quick Start para Desenvolvedores

```bash
# 1. Adicionar dependência ao pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.0</version>
</dependency>

# 2. Copiar classes Java (DTOs, Exception Handlers, Entities)
# 3. Atualizar Controllers com @Valid
# 4. Implementar lógica no Service Layer
# 5. Testar os 4 cenários

# Teste Cenário 1: Valor Negativo
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Test","saleValue":-100}'
# Esperado: 400 Bad Request

# Teste Cenário 2: Estoque Negativo
curl -X POST http://localhost:8080/api/v1/raw-materials \
  -H "Content-Type: application/json" \
  -d '{"code":"MAT-001","name":"Steel","stockQuantity":-50,"unitMeasure":"KG","unitCost":10}'
# Esperado: 400 Bad Request

# Teste Cenário 3: Código Duplicado (segunda requisição)
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Product B","saleValue":150}'
# Esperado: 409 Conflict

# Teste Cenário 4: Exclusão em Cascata
curl -X DELETE http://localhost:8080/api/v1/raw-materials/1
# Esperado: 204 No Content (se sem referências) ou 409 Conflict (se com referências)
```

---

## Arquitetura de Validação

```
DTO com @Valid
    ↓
GlobalExceptionHandler captura exceções
    ↓
Retorna JSON estruturado com status code apropriado

Validação em 3 camadas:
1. DTO (@Valid, @Positive, @PositiveOrZero)
2. Entity (@NotBlank, constraints do banco)
3. Database (Unique indexes, Foreign keys)
4. Business Logic (Service layer checks)
```

---

**Implementação Completa: ✅ PRONTA PARA PRODUÇÃO**

Data: 24 de Fevereiro de 2026  
Versão: 1.0  
Status: ✅ Entregue com Sucesso
