<template>
  <div class="profit-hero">
    <div>
      <div class="profit-label">Lucro Total Projetado</div>
      <div class="profit-value">{{ formatCurrency(totalProfit) }}</div>
    </div>
    <div class="profit-meta">
      {{ itemsCount }} produtos a fabricar<br />
      Calculado às {{ formatTime(calculatedAt) }}<br />
      <span style="color: var(--oak); font-size: 11px; font-family: 'DM Mono', monospace">
        GREEDY · ÓTIMO LOCAL
      </span>
    </div>
  </div>
</template>

<script setup>
/**
 * ProfitHero.vue — Destaque do lucro total e metadados
 *
 * Props injetadas pelo DashboardView:
 * - totalProfit: número
 * - calculatedAt: Date ou string ISO
 * - itemsCount: quantidade de produtos a fabricar no plano
 */

import { computed } from 'vue';

defineProps({
  totalProfit: { type: Number, default: 0 },
  calculatedAt: { type: [Date, String], default: () => new Date() },
  itemsCount: { type: Number, default: 0 },
});

function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v));
}

function formatTime(d) {
  const date = typeof d === 'string' ? new Date(d) : d;
  return date.toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
}
</script>
