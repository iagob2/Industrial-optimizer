package com.example.industrialoptimizer.repository;

import com.example.industrialoptimizer.model.ProductComposition;
import com.example.industrialoptimizer.model.ProductCompositionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCompositionRepository extends JpaRepository<ProductComposition, ProductCompositionKey> {

    @Query("SELECT c FROM ProductComposition c WHERE c.id.productId = :productId ORDER BY c.id.rawMaterialId")
    List<ProductComposition> findByProductId(@Param("productId") Long productId);

    /**
     * Count ProductComposition records referencing a specific RawMaterial.
     * Used in Scenario 4: Cascade Deletion Prevention
     *
     * @param rawMaterialId the ID of the RawMaterial
     * @return count of ProductComposition records using this RawMaterial
     */
    long countByRawMaterialId(Long rawMaterialId);
}
