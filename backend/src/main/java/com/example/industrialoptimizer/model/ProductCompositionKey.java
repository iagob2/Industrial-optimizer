package com.example.industrialoptimizer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable 

public class ProductCompositionKey implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "raw_material_id")
    private Long rawMaterialId;

    // O @Data do Lombok já cria o equals() e hashCode() para mim não preciso colocar 
}