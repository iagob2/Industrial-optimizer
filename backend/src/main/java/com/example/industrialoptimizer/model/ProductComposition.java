package com.example.industrialoptimizer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product_compositions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Evita erros de proxy
public class ProductComposition {

    @EmbeddedId
    private ProductCompositionKey id;

    @JsonIgnore
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    // REMOVIDO: fetch=LAZY e @JsonIgnore para o frontend/service conseguir ler o Insumo
    @ManyToOne
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @Column(name = "quantity_needed", precision = 15, scale = 3)
    @PositiveOrZero(message = "A quantidade deve ser positiva")
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
