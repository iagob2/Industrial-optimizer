<template>
  <div class="stock-list">
    <div v-for="m in materials" :key="m.id" class="stock-item">
      <div class="stock-header">
        <span class="stock-name">{{ m.name }}</span>
        <span class="stock-qty">
          {{ m.stockQuantity }}
          <span class="unit-pill">{{ m.unitMeasure }}</span>
        </span>
      </div>
      <div class="stock-bar-track">
        <div
          class="stock-bar-fill"
          :class="getStockLevel(m)"
          :style="{ width: getStockPct(m) + '%' }"
        ></div>
      </div>
      <span class="stock-badge" :class="getStockLevel(m)">
        {{ getStockLabel(m) }}
      </span>
    </div>
  </div>
</template>

<script setup>
/**
 * StockList.vue — Lista de estoque (sidebar)
 * materials + funções de nível/pct/label injetadas pelo DashboardView
 */
defineProps({
  materials: { type: Array, default: () => [] },
  getStockPct: { type: Function, required: true },
  getStockLevel: { type: Function, required: true },
  getStockLabel: { type: Function, required: true },
});
</script>
