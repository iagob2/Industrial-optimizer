package com.example.industrialoptimizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "raw_materials")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Raw material code cannot be blank")
    private String code;

    @Column(nullable = false)
    @NotBlank(message = "Raw material name cannot be blank")
    private String name;

    @Column(name = "stock_quantity", precision = 15, scale = 3)
    @NotNull(message = "Stock quantity cannot be null")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private BigDecimal stockQuantity;

    @Column(name = "unit_measure")
    @NotBlank(message = "Unit measure cannot be blank")
    private String unitMeasure;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    @NotNull(message = "Unit cost cannot be null")
    @PositiveOrZero(message = "Unit cost cannot be negative")
    private BigDecimal unitCost;

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

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
