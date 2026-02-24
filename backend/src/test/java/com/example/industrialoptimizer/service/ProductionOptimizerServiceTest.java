package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.model.ProductComposition;
import com.example.industrialoptimizer.model.ProductCompositionKey;
import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.repository.ProductRepository;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@DisplayName("Production Optimizer Service Test Suite")
class ProductionOptimizerServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductionOptimizerService productionOptimizerService;

    @BeforeEach
    void setUp() {
        log.info("Initializing test fixtures");
        MockitoAnnotations.openMocks(this);
    }

    // Abundance Scenario
    @Test
    @DisplayName("Should suggest production of all products when stock is infinite")
    void testAbundanceScenario() {
        log.info("Starting Abundance Scenario test");

        // Arrange
        RawMaterial materialA = createRawMaterial(1L, "MATERIAL_A", new BigDecimal("1000"));
        RawMaterial materialB = createRawMaterial(2L, "MATERIAL_B", new BigDecimal("1000"));

        Product productOne = createProduct(1L, "PRODUCT_ONE", new BigDecimal("500"));
        Product productTwo = createProduct(2L, "PRODUCT_TWO", new BigDecimal("300"));

        ProductComposition compOne = createComposition(productOne, materialA, new BigDecimal("2"));
        ProductComposition compTwo = createComposition(productTwo, materialB, new BigDecimal("3"));

        productOne.setCompositions(new ArrayList<>(List.of(compOne)));
        productTwo.setCompositions(new ArrayList<>(List.of(compTwo)));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(productOne, productTwo)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(materialA, materialB)));

        // Act
        Map<String, Integer> result = productionOptimizerService.calculateOptimalProduction();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should contain production suggestions");
        assertTrue(result.containsKey("PRODUCT_ONE"), "Should suggest PRODUCT_ONE");
        assertTrue(result.containsKey("PRODUCT_TWO"), "Should suggest PRODUCT_TWO");
        assertTrue(result.get("PRODUCT_ONE") > 0, "PRODUCT_ONE should have positive quantity");
        assertTrue(result.get("PRODUCT_TWO") > 0, "PRODUCT_TWO should have positive quantity");

        log.info("Abundance Scenario passed: {}", result);
    }

    // Total Scarcity
    @Test
    @DisplayName("Should return empty list when stock is zero without NullPointerException")
    void testTotalScarcityScenario() {
        log.info("Starting Total Scarcity Scenario test");

        // Arrange
        RawMaterial materialA = createRawMaterial(1L, "MATERIAL_A", BigDecimal.ZERO);
        RawMaterial materialB = createRawMaterial(2L, "MATERIAL_B", BigDecimal.ZERO);

        Product productOne = createProduct(1L, "PRODUCT_ONE", new BigDecimal("500"));
        ProductComposition compOne = createComposition(productOne, materialA, new BigDecimal("5"));
        productOne.setCompositions(new ArrayList<>(List.of(compOne)));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(productOne)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(materialA, materialB)));

        // Act
        Map<String, Integer> result = productionOptimizerService.calculateOptimalProduction();

        // Assert
        assertNotNull(result, "Result should not be null (no NullPointerException)");
        assertTrue(result.isEmpty(), "Result should be empty when stock is zero");

        log.info("Total Scarcity Scenario passed: empty production map");
    }

    // Value Prioritization
    @Test
    @DisplayName("Should prioritize higher-value product when competing for same raw material")
    void testValuePrioritizationScenario() {
        log.info("Starting Value Prioritization Scenario test");

        // Arrange
        RawMaterial commonMaterial = createRawMaterial(1L, "COMMON_MATERIAL", new BigDecimal("100"));

        // Product A: Sale Value = R$ 500
        Product productA = createProduct(1L, "PRODUCT_A", new BigDecimal("500"));
        ProductComposition compA = createComposition(productA, commonMaterial, new BigDecimal("10"));
        productA.setCompositions(new ArrayList<>(List.of(compA)));

        // Product B: Sale Value = R$ 100 (Lower value)
        Product productB = createProduct(2L, "PRODUCT_B", new BigDecimal("100"));
        ProductComposition compB = createComposition(productB, commonMaterial, new BigDecimal("10"));
        productB.setCompositions(new ArrayList<>(List.of(compB)));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(productA, productB)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(commonMaterial)));

        // Act
        Map<String, Integer> result = productionOptimizerService.calculateOptimalProduction();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.containsKey("PRODUCT_A"), "PRODUCT_A should be prioritized");
        assertTrue(result.get("PRODUCT_A") > 0, "PRODUCT_A should have positive quantity");
        assertTrue(!result.containsKey("PRODUCT_B") || result.get("PRODUCT_B") == 0,
                "PRODUCT_B should not be produced or have zero quantity");

        log.info("Value Prioritization Scenario passed: {}", result);
    }

    // Partial Consumption
    @Test
    @DisplayName("Should suggest partial production and preserve remaining stock")
    void testPartialConsumptionScenario() {
        log.info("Starting Partial Consumption Scenario test");

        // Arrange
        RawMaterial materialA = createRawMaterial(1L, "MATERIAL_A", new BigDecimal("10"));

        Product productA = createProduct(1L, "PRODUCT_A", new BigDecimal("500"));
        ProductComposition compA = createComposition(productA, materialA, new BigDecimal("3"));
        productA.setCompositions(new ArrayList<>(List.of(compA)));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(productA)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(materialA)));

        // Act
        Map<String, Integer> result = productionOptimizerService.calculateOptimalProduction();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.containsKey("PRODUCT_A"), "PRODUCT_A should be produced");
        int suggestedQuantity = result.get("PRODUCT_A");
        assertEquals(3, suggestedQuantity, "Should suggest 3 units (10 ÷ 3 = 3 with remainder)");

        log.info("Partial Consumption Scenario passed: Suggested {} units, preserving remaining stock",
                suggestedQuantity);
    }

    // Composite Materials
    @Test
    @DisplayName("Should return zero production when one of three required materials is missing")
    void testCompositeMaterialsScenario() {
        log.info("Starting Composite Materials Scenario test");

        // Arrange
        RawMaterial materialA = createRawMaterial(1L, "MATERIAL_A", new BigDecimal("100"));
        RawMaterial materialB = createRawMaterial(2L, "MATERIAL_B", new BigDecimal("100"));
        RawMaterial materialC = createRawMaterial(3L, "MATERIAL_C", BigDecimal.ZERO); // Missing

        Product productComplex = createProduct(1L, "COMPLEX_PRODUCT", new BigDecimal("1000"));

        ProductComposition compA = createComposition(productComplex, materialA, new BigDecimal("5"));
        ProductComposition compB = createComposition(productComplex, materialB, new BigDecimal("5"));
        ProductComposition compC = createComposition(productComplex, materialC, new BigDecimal("5"));

        productComplex.setCompositions(new ArrayList<>(List.of(compA, compB, compC)));

        when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(productComplex)));
        when(rawMaterialRepository.findAll()).thenReturn(new ArrayList<>(List.of(materialA, materialB, materialC)));

        // Act
        Map<String, Integer> result = productionOptimizerService.calculateOptimalProduction();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty() || !result.containsKey("COMPLEX_PRODUCT") || result.get("COMPLEX_PRODUCT") == 0,
                "COMPLEX_PRODUCT should not be produced when any required material is missing");

        log.info("Composite Materials Scenario passed: Production blocked due to missing material");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Creates a RawMaterial instance for testing purposes.
     *
     * @param id            the material ID
     * @param name          the material name
     * @param stockQuantity the available stock quantity
     * @return a configured RawMaterial instance
     */
    private RawMaterial createRawMaterial(Long id, String name, BigDecimal stockQuantity) {
        RawMaterial material = new RawMaterial();
        material.setId(id);
        material.setCode("CODE_" + name);
        material.setName(name);
        material.setStockQuantity(stockQuantity);
        material.setUnitMeasure("UNIT");
        material.setUnitCost(new BigDecimal("10"));
        return material;
    }

    /**
     * Creates a Product instance for testing purposes.
     *
     * @param id        the product ID
     * @param name      the product name
     * @param saleValue the sale value of the product
     * @return a configured Product instance
     */
    private Product createProduct(Long id, String name, BigDecimal saleValue) {
        Product product = new Product();
        product.setId(id);
        product.setCode("CODE_" + name);
        product.setName(name);
        product.setSaleValue(saleValue);
        product.setCompositions(new ArrayList<>());
        return product;
    }

    /**
     * Creates a ProductComposition instance linking a product to a raw material.
     *
     * @param product        the product
     * @param rawMaterial    the raw material
     * @param quantityNeeded the quantity of raw material needed per unit of product
     * @return a configured ProductComposition instance
     */
    private ProductComposition createComposition(Product product, RawMaterial rawMaterial, BigDecimal quantityNeeded) {
        ProductComposition composition = new ProductComposition();
        ProductCompositionKey key = new ProductCompositionKey();
        key.setProductId(product.getId());
        key.setRawMaterialId(rawMaterial.getId());

        composition.setId(key);
        composition.setProduct(product);
        composition.setRawMaterial(rawMaterial);
        composition.setQuantityNeeded(quantityNeeded);

        return composition;
    }
}
