# 🧠 Optimization Logic & Algorithmic Theory
> Explicação matemática e lógica do cálculo de sugestão de produção.

## 1. Definição do Problema
O sistema enfrenta um problema clássico de **Otimização Combinatória**: como alocar recursos limitados (matérias-primas) para maximizar uma função objetivo (valor total de venda).

## 2. Estratégia: Algoritmo Guloso (Greedy Algorithm)
Para este desafio, implementamos uma **Estratégia Gulosa**. Um algoritmo guloso é aquele que faz a escolha localmente ótima em cada etapa com a esperança de encontrar um ótimo global.

### Passo a Passo da Lógica:
1. **Ordenação por Valor (Heurística):** Os produtos são classificados em ordem decrescente de `sale_value`. Isso garante que o capital de giro seja priorizado para os itens de maior retorno.
2. **Simulação de Consumo:** O sistema itera sobre a lista ordenada, tentando produzir o máximo de unidades de cada item enquanto houver estoque disponível na simulação em memória.
3. **Resolução de Conflitos:** Itens de menor valor só recebem insumos se os itens de maior valor já tiverem exaurido sua capacidade de produção ou se restarem sobras de estoque.

## 3. Análise de Complexidade
* **Tempo:** $O(N \log N)$ para a ordenação dos produtos, seguido de $O(N \times M)$ para a iteração de estoque, onde $N$ é o número de produtos e $M$ a complexidade da composição.
* **Espaço:** $O(K)$, onde $K$ é o número de matérias-primas armazenadas no mapa temporário de cálculo.

## 4. Fontes e Referências Teóricas
* **Algoritmos Gulosos:** Teoria baseada em *Introduction to Algorithms (CLRS)*, especificamente no capítulo sobre *Greedy Algorithms*.
* **Otimização Industrial:** Conceitos de *Material Requirements Planning (MRP)* aplicados à fabricação Just-in-Time.