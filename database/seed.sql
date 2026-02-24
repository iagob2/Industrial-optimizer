-- SEED DATA PARA INDUSTRIAL OPTIMIZER

-- Inserindo Insumos (Raw Materials)
INSERT INTO raw_materials (code, name, stock_quantity, unit_measure, unit_cost) VALUES
('WOOD-01', 'Tábua de Carvalho', 50.000, 'unit', 45.00),
('WOOD-02', 'Placa de MDF', 100.000, 'unit', 25.00),
('METAL-01', 'Barra de Aço 1m', 30.000, 'unit', 60.00),
('GLUE-01', 'Cola Industrial', 10.000, 'kg', 15.00),
('PAINT-01', 'Verniz Brilhante', 20.000, 'L', 35.00),
('SCREW-01', 'Parafuso Philips', 500.000, 'unit', 0.10),
('FABRIC-01', 'Tecido Algodão', 40.000, 'm', 20.00),
('FOAM-01', 'Espuma Densidade 33', 15.000, 'm2', 55.00);

-- Inserindo Produtos (Products)
-- Criamos produtos com diferentes margens para testar o algoritmo de otimização
INSERT INTO products (code, name, sale_value) VALUES
('PROD-OFFICE-01', 'Mesa de Escritório Luxo', 1200.00),
('PROD-CHAIR-01', 'Cadeira Ergonômica', 850.00),
('PROD-SHELF-01', 'Estante de Livros', 450.00),
('PROD-STOOL-01', 'Banco de Madeira Simples', 150.00),
('PROD-SOFA-01', 'Poltrona Individual', 950.00),
('PROD-FRAME-01', 'Quadro Decorativo', 120.00);

-- Inserindo Composições (Product Composition - A "Receita")
-- Mesa de Escritório: Usa muito Carvalho e Aço
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(1, 1, 4.000), -- 4 Tábuas de Carvalho
(1, 3, 2.000), -- 2 Barras de Aço
(1, 6, 12.000), -- 12 Parafusos
(1, 5, 0.500); -- 0.5L de Verniz

-- Cadeira Ergonômica: Disputa Aço e Tecido
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(2, 3, 1.000), -- 1 Barra de Aço
(2, 7, 2.000), -- 2m Tecido
(2, 8, 1.000), -- 1m2 Espuma
(2, 6, 8.000);  -- 8 Parafusos

-- Estante de Livros: Usa MDF (mais barato)
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(3, 2, 5.000), -- 5 Placas MDF
(3, 6, 20.000), -- 20 Parafusos
(3, 4, 0.200);  -- 0.2kg Cola

-- Banco Simples: Opção de baixo custo
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(4, 2, 1.000), -- 1 Placa MDF
(4, 6, 4.000),  -- 4 Parafusos
(4, 4, 0.100);  -- 0.1kg Cola

-- Poltrona: Disputa Espuma e Tecido com a Cadeira
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(5, 7, 5.000), -- 5m Tecido
(5, 8, 3.000), -- 3m2 Espuma
(5, 1, 1.000);  -- 1 Tábua Carvalho (pés)

-- Quadro: Usa restos de materiais
INSERT INTO product_compositions (product_id, raw_material_id, quantity_needed) VALUES
(6, 2, 0.500), -- 0.5 Placa MDF
(6, 5, 0.100);  -- 0.1L Verniz