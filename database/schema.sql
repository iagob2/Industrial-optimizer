-- 1. Tabela de Insumos (Raw Materials)
CREATE TABLE raw_materials (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    stock_quantity DECIMAL(15, 3) NOT NULL DEFAULT 0,
    unit_measure VARCHAR(10) NOT NULL,
    unit_cost DECIMAL(15, 2) NOT NULL DEFAULT 0.00 -- Adicionado com correção de vírgula
);

-- 2. Tabela de Produtos (Products)
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    sale_value DECIMAL(15, 2) NOT NULL DEFAULT 0.00
);

-- 3. Tabela Associativa de Composição (Product Composition)
CREATE TABLE product_compositions (
    product_id INTEGER NOT NULL,
    raw_material_id INTEGER NOT NULL,
    quantity_needed DECIMAL(15, 3) NOT NULL,
    
    PRIMARY KEY (product_id, raw_material_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_material FOREIGN KEY (raw_material_id) REFERENCES raw_materials(id) ON DELETE CASCADE
);

CREATE INDEX idx_composition_product ON product_compositions(product_id);