package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.dto.ProductDTO;
import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public List<Product> getAllProducts() {
        return getAll();
    }

    public Product getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    public Product getProductById(Long id) {
        return getById(id);
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Product createProduct(ProductDTO productDTO) {
        log.info("Creating product: {}", productDTO.getCode());
        Product product = new Product();
        product.setCode(productDTO.getCode());
        product.setName(productDTO.getName());
        product.setSaleValue(productDTO.getSaleValue());
        return save(product);
    }

    public Product update(Long id, Product product) {
        Product existing = getById(id);
        existing.setCode(product.getCode());
        existing.setName(product.getName());
        existing.setSaleValue(product.getSaleValue());
        return repository.save(existing);
    }

    public Product updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product: {}", id);
        Product existing = getById(id);
        existing.setCode(productDTO.getCode());
        existing.setName(productDTO.getName());
        existing.setSaleValue(productDTO.getSaleValue());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id);
        }
        repository.deleteById(id);
    }

    public void deleteProduct(Long id) {
        delete(id);
    }
}
