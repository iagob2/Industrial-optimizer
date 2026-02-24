package com.example.industrialoptimizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO para ProductComposition com informações completas de RawMaterial.
 * Evita circular references enquanto fornece dados úteis ao frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCompositionDTO {

    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("rawMaterialId")
    private Long rawMaterialId;

    @JsonProperty("rawMaterialCode")
    private String rawMaterialCode;

    @JsonProperty("rawMaterialName")
    private String rawMaterialName;

    @JsonProperty("rawMaterialUnitMeasure")
    private String rawMaterialUnitMeasure;

    @JsonProperty("rawMaterialUnitCost")
    private BigDecimal rawMaterialUnitCost;

    @JsonProperty("quantityNeeded")
    private BigDecimal quantityNeeded;

    @JsonProperty("totalCost")
    private BigDecimal totalCost; // quantityNeeded * unitCost
}
