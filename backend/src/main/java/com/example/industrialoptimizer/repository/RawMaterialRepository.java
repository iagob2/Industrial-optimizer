package com.example.industrialoptimizer.repository;

import com.example.industrialoptimizer.model.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    // O JpaRepository já provê métodos como:
    // .findAll() -> Listar todos os insumos
    // .findById() -> Achar um específico
    // .save() -> Atualizar o estoque
}