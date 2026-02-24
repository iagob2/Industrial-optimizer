<template>
  <div class="page">
    <div class="stats-row">
      <div class="stat-box">
        <div class="stat-value">{{ products.length }}</div>
        <div class="stat-key">Produtos Cadastrados</div>
      </div>
      <div class="stat-box">
        <div class="stat-value">{{ materials.length }}</div>
        <div class="stat-key">Tipos de Insumos</div>
      </div>
      <div class="stat-box">
        <div class="stat-value oak">{{ criticalCount }}</div>
        <div class="stat-key">Insumos em Alerta</div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div>
        <!-- CtaCard: botão dispara evento; Dashboard reage chamando a API -->
        <CtaCard
          :loading="isLoading"
          @optimize="calculateOptimization"
        />

        <div v-if="error" class="limitation-card">
          <div class="limitation-header">Erro na API</div>
          <div class="limitation-item">{{ error }}</div>
        </div>

        <!-- Painel de resultado: dados injetados via props nos filhos -->
        <div v-if="result" class="result-panel">
          <ProfitHero
            :total-profit="result.totalProfit"
            :calculated-at="result.calculatedAt"
            :items-count="result.items.filter((i) => i.canProduce).length"
          />

          <LimitationAlert v-if="result.limitations.length > 0" :items="result.limitations" />

          <div class="card-label" style="margin-bottom: 16px">Plano de Produção Recomendado</div>
          <div class="production-grid">
            <ProductionCard
              v-for="item in result.items"
              :key="item.id"
              :item="item"
              :format-currency="formatCurrency"
            />
          </div>

          <div class="card consumption-section" style="margin-top: 0">
            <div class="consumption-title">Consumo de Estoque pelo Plano</div>
            <StockConsumption :consumption="stockConsumption" />
          </div>
        </div>
      </div>

      <div>
        <div class="card">
          <div class="card-label">Estoque Atual de Insumos</div>
          <StockList
            :materials="materials"
            :get-stock-pct="getStockPct"
            :get-stock-level="getStockLevel"
            :get-stock-label="getStockLabel"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * DashboardView.vue — Componente pai inteligente (controlador)
 *
 * Responsabilidades:
 * - Estado central: materials, products, result, isLoading, error
 * - Buscar dados iniciais da API (raw-materials, products)
 * - calculateOptimization(): chamada GET /api/products/suggest e montagem do objeto result
 * - Distribuição de dados para os filhos via props
 * - Reação ao evento @optimize emitido pelo CtaCard
 */

import { ref, computed, onMounted } from 'vue';
import { api } from '../api/client';
import CtaCard from '../components/optimization/CtaCard.vue';
import ProfitHero from '../components/optimization/ProfitHero.vue';
import ProductionCard from '../components/optimization/ProductionCard.vue';
import LimitationAlert from '../components/optimization/LimitationAlert.vue';
import StockConsumption from '../components/optimization/StockConsumption.vue';
import StockList from '../components/shared/StockList.vue';

// ─── Estado reativo (fonte única da verdade) ─────────────────────────────
const materials = ref([]);
const products = ref([]);
const result = ref(null);
const isLoading = ref(false);
const error = ref(null);

// ─── Carregar dados iniciais ao montar a view ───────────────────────────
onMounted(async () => {
  try {
    const [mats, prods] = await Promise.all([api.getMaterials(), api.getProducts()]);
    materials.value = mats;
    products.value = prods;
  } catch (e) {
    error.value = e.message || 'Falha ao carregar dados. Verifique se o backend está rodando.';
  }
});

// ─── Helpers de exibição (passados como props onde necessário) ───────────
function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v));
}

function getStockPct(m) {
  const max = m.maxStock ?? Math.max(Number(m.stockQuantity) * 2, 100);
  return Math.min(100, Math.round((Number(m.stockQuantity) / max) * 100));
}

function getStockLevel(m) {
  const pct = getStockPct(m);
  if (pct >= 50) return 'high ok';
  if (pct >= 20) return 'mid alerta';
  return 'low critico';
}

function getStockLabel(m) {
  const pct = getStockPct(m);
  if (pct >= 50) return 'ok';
  if (pct >= 20) return 'alerta';
  return 'critico';
}

const criticalCount = computed(() =>
  materials.value.filter((m) => getStockPct(m) < 50).length
);

/**
 * Monta o objeto "result" esperado pelos componentes a partir da resposta
 * do backend GET /api/products/suggest (Map productName -> quantity).
 */
function buildResultFromSuggest(suggest, productList) {
  const items = [];
  let totalProfit = 0;
  const limitations = [];

  for (const product of productList) {
    const qty = suggest[product.name] ?? 0;
    const saleValue = Number(product.saleValue);
    const canProduce = qty > 0;
    const total = qty * saleValue;
    totalProfit += total;

    let reason = null;
    if (!canProduce) {
      reason = 'Estoque insuficiente para este produto';
      limitations.push(`Estoque insuficiente para ${product.name}`);
    }

    items.push({
      id: product.id,
      name: product.name,
      quantity: qty,
      unitProfit: saleValue,
      totalProfit: total,
      canProduce,
      reason,
    });
  }

  return {
    totalProfit,
    calculatedAt: new Date(),
    limitations,
    items,
  };
}

/**
 * Função chamada quando o usuário clica em "Otimizar" no CtaCard.
 * Faz GET na API de sugestão e atualiza o estado result.
 */
async function calculateOptimization() {
  error.value = null;
  result.value = null;
  isLoading.value = true;

  try {
    const suggest = await api.getOptimization();
    result.value = buildResultFromSuggest(suggest, products.value);
  } catch (e) {
    error.value = e.message || 'Erro ao calcular otimização.';
  } finally {
    isLoading.value = false;
  }
}

// Consumo de estoque pelo plano (para as barras)
const stockConsumption = computed(() => {
  if (!result.value) return [];

  const consumed = {};
  for (const item of result.value.items) {
    if (!item.canProduce) continue;
    const product = products.value.find((p) => p.id === item.id);
    if (!product?.compositions) continue;
    for (const comp of product.compositions) {
      const name = comp.rawMaterial?.name;
      if (!name) continue;
      const qty = Number(comp.quantityNeeded) * item.quantity;
      consumed[name] = (consumed[name] || 0) + qty;
    }
  }

  return Object.entries(consumed)
    .map(([name, qty]) => {
      const mat = materials.value.find((m) => m.name === name);
      const original = mat ? Number(mat.stockQuantity) : 1;
      const pct = Math.min(100, Math.round((qty / original) * 100));
      return { name, pct };
    })
    .sort((a, b) => b.pct - a.pct);
});
</script>
