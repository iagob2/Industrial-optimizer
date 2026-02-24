package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class RawMaterialService {

    private final RawMaterialRepository repository;

    public RawMaterialService(RawMaterialRepository repository) {
        this.repository = repository;
    }

    public List<RawMaterial> getAll() {
        return repository.findAll();
    }

    public RawMaterial getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raw material not found: " + id));
    }

    public RawMaterial save(RawMaterial material) {
        return repository.save(material);
    }

    public RawMaterial update(Long id, RawMaterial material) {
        RawMaterial existing = getById(id);
        existing.setCode(material.getCode());
        existing.setName(material.getName());
        existing.setStockQuantity(material.getStockQuantity());
        existing.setUnitMeasure(material.getUnitMeasure());
        existing.setUnitCost(material.getUnitCost());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Raw material not found: " + id);
        }
        repository.deleteById(id);
    }
}