package com.example.industrialoptimizer.service;

import com.example.industrialoptimizer.model.Product;
import com.example.industrialoptimizer.model.ProductComposition;
import com.example.industrialoptimizer.repository.ProductRepository;
import com.example.industrialoptimizer.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductionOptimizerService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionOptimizerService(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public Map<String, Integer> calculateOptimalProduction() {
        // 1. Busca TODOS os produtos 
        List<Product> products = productRepository.findAll();

        // 2. Ordena os produtos pelo LUCRO (Valor de Venda - Custo Total) de forma decrescente
        products.sort((p1, p2) -> {
            BigDecimal profit1 = calculateProfit(p1);
            BigDecimal profit2 = calculateProfit(p2);
            return profit2.compareTo(profit1); // Ordem decrescente (do maior lucro para o menor)
        });

        // 3. Cria um mapa de estoque em memória para a simulação
        Map<Long, BigDecimal> availableStock = new HashMap<>();
        rawMaterialRepository.findAll().forEach(material -> 
            availableStock.put(material.getId(), material.getStockQuantity())
        );

        // 4. Resultado: Nome do Produto -> Quantidade sugerida
        Map<String, Integer> suggestedProduction = new LinkedHashMap<>();

        // 5. Algoritmo de Otimização 
        for (Product product : products) {
            int count = 0;
            boolean canProduce = true;

            while (canProduce) {
                // Verifica se há estoque para produzir UMA unidade
                for (ProductComposition composition : product.getCompositions()) {
                    BigDecimal stock = availableStock.get(composition.getRawMaterial().getId());
                    if (stock == null || stock.compareTo(composition.getQuantityNeeded()) < 0) {
                        canProduce = false;
                        break;
                    }
                }

                // Se houver estoque, subtrai os insumos e incrementa a produção
                if (canProduce) {
                    for (ProductComposition composition : product.getCompositions()) {
                        Long materialId = composition.getRawMaterial().getId();
                        BigDecimal currentStock = availableStock.get(materialId);
                        availableStock.put(materialId, currentStock.subtract(composition.getQuantityNeeded()));
                    }
                    count++;
                }
            }

            if (count > 0) {
                suggestedProduction.put(product.getName(), count);
            }
        }

        return suggestedProduction;
    }

    // --- MÉTODO AUXILIAR PARA CALCULAR O LUCRO ---
    private BigDecimal calculateProfit(Product product) {
        BigDecimal totalCost = BigDecimal.ZERO;
        
        // Soma o custo de todos os ingredientes (Quantidade * Preço Unitário do Insumo)
        for (ProductComposition comp : product.getCompositions()) {
            BigDecimal qty = comp.getQuantityNeeded();
            BigDecimal unitCost = comp.getRawMaterial().getUnitCost();
            
            BigDecimal costForThisMaterial = qty.multiply(unitCost);
            totalCost = totalCost.add(costForThisMaterial);
        }
        
        // Lucro = Valor de Venda - Custo Total
        return product.getSaleValue().subtract(totalCost);
    }
}