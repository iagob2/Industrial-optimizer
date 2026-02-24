package com.example.industrialoptimizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "products")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /**
     * Cascade Configuration for Product Deletion:
     * - CascadeType.ALL: When a Product is deleted, all ProductComposition entries
     * are also deleted.
     * - orphanRemoval = true: Removes ProductComposition records when they are no
     * longer referenced.
     * 
     * This is SAFE because:
     * - A ProductComposition exists ONLY to link a Product to a RawMaterial
     * - Removing a Product should remove its recipes
     * - RawMaterial is NOT deleted (it can be used in other products)
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductComposition> compositions;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSaleValue() {
        return saleValue;
    }

    public void setSaleValue(BigDecimal saleValue) {
        this.saleValue = saleValue;
    }

    public List<ProductComposition> getCompositions() {
        return compositions;
    }

    public void setCompositions(List<ProductComposition> compositions) {
        this.compositions = compositions;
    }
}
