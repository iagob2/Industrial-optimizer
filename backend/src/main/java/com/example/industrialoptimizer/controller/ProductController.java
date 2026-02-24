package com.example.industrialoptimizer.controller;

import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.service.ProductService;
import com.example.industrialoptimizer.service.ProductionOptimizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;
    private final ProductionOptimizerService optimizerService;

    // Construtor ÚNICO injetando os dois serviços ao mesmo tempo
    public ProductController(ProductService service, ProductionOptimizerService optimizerService) {
        this.service = service;
        this.optimizerService = optimizerService;
    }

    // ==========================================
    // ROTA DE OTIMIZAÇÃO 
    // ==========================================
    
    @GetMapping("/suggest")
    public Map<String, Integer> getSuggestedProduction() {
        return optimizerService.calculateOptimalProduction();
    }

    // ==========================================
    // ROTAS DE CRUD (O Básico)
    // ==========================================

    @GetMapping
    public List<Product> listAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(service.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}