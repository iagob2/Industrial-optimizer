package com.example.industrialoptimizer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product_compositions")
public class ProductComposition {

    @EmbeddedId
    private ProductCompositionKey id;

    /** Evita referência circular na serialização JSON (Product -> compositions -> product -> ...). */
    @JsonIgnore
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @Column(name = "quantity_needed", precision = 15, scale = 3)
    private BigDecimal quantityNeeded;

    // Getters and Setters
    public ProductCompositionKey getId() {
        return id;
    }

    public void setId(ProductCompositionKey id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public BigDecimal getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(BigDecimal quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }
}
