package com.example.industrialoptimizer.repository;

import com.example.industrialoptimizer.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Busca todos os produtos ordenando do maior valor para o menor
    List<Product> findAllByOrderBySaleValueDesc();
}