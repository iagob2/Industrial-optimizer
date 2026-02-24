# Testes do Painel de Otimização (Frontend)

Este documento descreve como foram implementados os testes automatizados do painel de otimização
(`DashboardView.vue`), agrupados no arquivo `frontend/src/__tests__/OptimizationPanel.spec.ts`
utilizando **Vitest** e **Vue Test Utils**.

## Ferramentas

- **Vitest**: runner de testes (`describe`, `it`, `expect`, `vi`).
- **@vue/test-utils**: montagem de componentes Vue (`mount`, `flushPromises`).
- **Mocks da API**: `vi.mock('../api/client')` para controlar `getMaterials`, `getProducts`,
  `getOptimization` e `getComposition`.

## Cenários Cobertos

1. **Loading State**
   - Ao clicar no botão principal de otimização (dentro de `CtaCard.vue`), o teste:
     - Garante que o botão não está desabilitado inicialmente.
     - Dispara o clique e mantém a Promise de `getOptimization` pendente (deferred).
     - Verifica que o botão passa a ter `disabled` e o texto `"Calculando..."`.
     - Após resolver a Promise, verifica que o botão volta ao estado normal e o texto retorna para
       `"Otimizar Produção (Maximizar Lucros)"`.

2. **Currency Formatting**
   - Com `getOptimization` retornando `{ 'Mesa de Escritório Luxo': 1 }`, o teste:
     - Monta o `DashboardView`, aguarda o carregamento inicial e clica no botão.
     - Após a resolução da Promise, checa o elemento `.profit-value`.
     - Valida que o texto contém `"R$ 1.200,00"` no formato BRL.

3. **Responsiveness**
   - Verifica se o container do grid de produção possui a classe responsiva `md:grid-cols-2`:
     - Monta o componente e simula uma otimização bem-sucedida.
     - Seleciona o elemento com classe `.production-grid`.
     - Asserta que `classes()` contém `"md:grid-cols-2"`, garantindo a aplicação das classes de
       responsividade.

4. **Error Feedback**
   - Simula uma falha na API de otimização:
     - `getOptimization` é mockado com `mockRejectedValue(new Error('500 Internal Server Error'))`.
     - Após o clique no botão, o teste aguarda `flushPromises()` e verifica a presença do bloco
       `.limitation-card`.
     - Garante que o texto contém mensagem clara de erro para o usuário (por exemplo,
       `"Erro na API"` ou `"Erro ao calcular otimização"`).

## Fluxo de Execução dos Testes

1. **Mock da API**
   - Antes de cada teste, `beforeEach` reseta os mocks e define:
     - `getMaterials` e `getProducts` com dados mínimos estáveis.
     - `getComposition` para alimentar o cálculo de consumo de estoque.

2. **Montagem do Componente**
   - O componente `DashboardView.vue` é montado diretamente, pois ele funciona como o
     **“Optimization Panel”** real, orquestrando:
     - O botão (`CtaCard`),
     - O destaque de lucro (`ProfitHero`),
     - A lista de produtos recomendados (`ProductionCard`),
     - E o painel de consumo (`StockConsumption`).

3. **Sincronização Assíncrona**
   - `flushPromises()` é usado após a montagem e após cliques para:
     - Aguardar `onMounted` (carregamento inicial).
     - Aguardar a resolução das Promises de API (mockadas).

Com isso, os testes garantem a UX principal do painel de otimização: estados de loading seguros,
formatação correta de valores financeiros, layout responsivo e feedback claro em caso de erro. 

