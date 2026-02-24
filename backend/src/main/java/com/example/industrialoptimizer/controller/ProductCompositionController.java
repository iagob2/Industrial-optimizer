package com.example.industrialoptimizer.controller;

import com.example.industrialoptimizer.dto.ProductCompositionDTO;
import com.example.industrialoptimizer.model.ProductComposition;
import com.example.industrialoptimizer.service.ProductCompositionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-compositions")
@CrossOrigin(origins = "*")
public class ProductCompositionController {

    private final ProductCompositionService service;

    public ProductCompositionController(ProductCompositionService service) {
        this.service = service;
    }

    /**
     * GET /api/product-compositions?productId=1
     * Retorna todas as composições de um produto com detalhes da matéria-prima.
     * 
     * @param productId ID do produto (obrigatório)
     * @return Lista de composições com informações da matéria-prima
     */
    @GetMapping
    public List<ProductCompositionDTO> listByProduct(@RequestParam(required = true) Long productId) {
        if (productId == null || productId <= 0) {
            throw new ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "productId é obrigatório e deve ser maior que 0"
            );
        }
        return service.findByProductIdWithDetails(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductComposition create(@RequestBody Map<String, Object> body) {
        Long productId = longFrom(body.get("productId"));
        Long rawMaterialId = longFrom(body.get("rawMaterialId"));
        BigDecimal quantityNeeded = decimalFrom(body.get("quantityNeeded"));
        return service.create(productId, rawMaterialId, quantityNeeded);
    }

    @PutMapping("/{productId}/{rawMaterialId}")
    public ProductComposition update(@PathVariable Long productId,
            @PathVariable Long rawMaterialId,
            @RequestBody Map<String, Object> body) {
        BigDecimal quantityNeeded = decimalFrom(body.get("quantityNeeded"));
        return service.update(productId, rawMaterialId, quantityNeeded);
    }

    @DeleteMapping("/{productId}/{rawMaterialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long productId, @PathVariable Long rawMaterialId) {
        service.delete(productId, rawMaterialId);
    }

    private static Long longFrom(Object o) {
        if (o == null)
            return null;
        if (o instanceof Number)
            return ((Number) o).longValue();
        return Long.parseLong(o.toString());
    }

    private static BigDecimal decimalFrom(Object o) {
        if (o == null)
            return BigDecimal.ZERO;
        if (o instanceof BigDecimal)
            return (BigDecimal) o;
        if (o instanceof Number)
            return BigDecimal.valueOf(((Number) o).doubleValue());
        return new BigDecimal(o.toString());
    }
}
