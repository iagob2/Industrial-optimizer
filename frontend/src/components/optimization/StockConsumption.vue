<template>
  <div v-for="c in consumption" :key="c.name" class="consumption-item">
    <div class="consumption-name">{{ c.name }}</div>
    <div class="consumption-track">
      <div class="consumption-fill" :style="{ width: c.pct + '%' }"></div>
    </div>
    <div class="consumption-pct">{{ c.pct }}%</div>
  </div>
</template>

<script setup>
/**
 * StockConsumption.vue — Barras de consumo de estoque pelo plano
 * consumption: [{ name, pct }, ...] passado pelo DashboardView (computed stockConsumption)
 */
defineProps({
  consumption: {
    type: Array,
    default: () => [],
  },
});
</script>
