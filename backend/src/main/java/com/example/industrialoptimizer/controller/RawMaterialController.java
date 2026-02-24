package com.example.industrialoptimizer.controller;

import com.example.industrialoptimizer.model.RawMaterial;
import com.example.industrialoptimizer.service.RawMaterialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@CrossOrigin(origins = "*")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService service) {
        this.service = service;
    }

    @GetMapping
    public List<RawMaterial> listAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterial> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public RawMaterial create(@RequestBody RawMaterial material) {
        return service.save(material);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterial> update(@PathVariable Long id, @RequestBody RawMaterial material) {
        return ResponseEntity.ok(service.update(id, material));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}