package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.model.ProductComposition;
import com.example.industrialoptimizer.model.ProductCompositionKey;
import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.repository.ProductCompositionRepository;
import com.example.industrialoptimizer.repository.ProductRepository;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductCompositionService {

    private final ProductCompositionRepository compositionRepository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductCompositionService(ProductCompositionRepository compositionRepository,
                                    ProductRepository productRepository,
                                    RawMaterialRepository rawMaterialRepository) {
        this.compositionRepository = compositionRepository;
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public List<ProductComposition> findByProductId(Long productId) {
        return compositionRepository.findByProductId(productId);
    }

    public ProductComposition create(Long productId, Long rawMaterialId, BigDecimal quantityNeeded) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + productId));
        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado: " + rawMaterialId));

        ProductCompositionKey key = new ProductCompositionKey(productId, rawMaterialId);
        if (compositionRepository.existsById(key)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta receita (produto + insumo) já existe.");
        }

        ProductComposition comp = new ProductComposition();
        comp.setId(key);
        comp.setProduct(product);
        comp.setRawMaterial(rawMaterial);
        comp.setQuantityNeeded(quantityNeeded != null ? quantityNeeded : BigDecimal.ZERO);
        return compositionRepository.save(comp);
    }

    public ProductComposition update(Long productId, Long rawMaterialId, BigDecimal quantityNeeded) {
        ProductCompositionKey key = new ProductCompositionKey(productId, rawMaterialId);
        ProductComposition comp = compositionRepository.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Composição não encontrada."));
        comp.setQuantityNeeded(quantityNeeded != null ? quantityNeeded : BigDecimal.ZERO);
        return compositionRepository.save(comp);
    }

    public void delete(Long productId, Long rawMaterialId) {
        ProductCompositionKey key = new ProductCompositionKey(productId, rawMaterialId);
        if (!compositionRepository.existsById(key)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Composição não encontrada.");
        }
        compositionRepository.deleteById(key);
    }
}
