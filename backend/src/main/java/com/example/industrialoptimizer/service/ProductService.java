package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product update(Long id, Product product) {
        Product existing = getById(id);
        existing.setCode(product.getCode());
        existing.setName(product.getName());
        existing.setSaleValue(product.getSaleValue());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id);
        }
        repository.deleteById(id);
    }
}

