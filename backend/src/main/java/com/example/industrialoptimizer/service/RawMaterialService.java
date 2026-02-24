package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.dto.RawMaterialDTO;
import com.example.industrialoptimizer.exception.CascadeDeletionException;
import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.repository.ProductCompositionRepository;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class RawMaterialService {

    private final RawMaterialRepository repository;
    private final ProductCompositionRepository productCompositionRepository;

    public RawMaterialService(RawMaterialRepository repository,
            ProductCompositionRepository productCompositionRepository) {
        this.repository = repository;
        this.productCompositionRepository = productCompositionRepository;
    }

    public List<RawMaterial> getAll() {
        return repository.findAll();
    }

    public List<RawMaterial> getAllRawMaterials() {
        return getAll();
    }

    public RawMaterial getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Raw material not found: " + id));
    }

    public RawMaterial save(RawMaterial material) {
        return repository.save(material);
    }

    public RawMaterial createRawMaterial(RawMaterialDTO rawMaterialDTO) {
        log.info("Creating raw material: {}", rawMaterialDTO.getCode());
        RawMaterial material = new RawMaterial();
        material.setCode(rawMaterialDTO.getCode());
        material.setName(rawMaterialDTO.getName());
        material.setStockQuantity(rawMaterialDTO.getStockQuantity());
        material.setUnitMeasure(rawMaterialDTO.getUnitMeasure());
        material.setUnitCost(rawMaterialDTO.getUnitCost());
        return save(material);
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

    public RawMaterial updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO) {
        log.info("Updating raw material: {}", id);
        RawMaterial existing = getById(id);
        existing.setCode(rawMaterialDTO.getCode());
        existing.setName(rawMaterialDTO.getName());
        existing.setStockQuantity(rawMaterialDTO.getStockQuantity());
        existing.setUnitMeasure(rawMaterialDTO.getUnitMeasure());
        existing.setUnitCost(rawMaterialDTO.getUnitCost());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Raw material not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteRawMaterial(Long rawMaterialId) {
        log.info("Attempting to delete raw material: {}", rawMaterialId);

        RawMaterial rawMaterial = getById(rawMaterialId);

        // Cascade Deletion Prevention: Check for references
        long compositionCount = productCompositionRepository.countByRawMaterialId(rawMaterialId);

        if (compositionCount > 0) {
            log.warn("Cannot delete raw material {}: has {} product composition references",
                    rawMaterial.getCode(), compositionCount);

            String errorMessage = String.format(
                    "Cannot delete raw material %s: it is used in %d product recipe(s). " +
                            "Remove all product compositions first.",
                    rawMaterial.getCode(),
                    compositionCount);

            throw new CascadeDeletionException(errorMessage);
        }

        log.info("Raw material {} has no references. Proceeding with deletion.", rawMaterial.getCode());
        repository.deleteById(rawMaterialId);
        log.info("Raw material {} deleted successfully.", rawMaterial.getCode());
    }
}