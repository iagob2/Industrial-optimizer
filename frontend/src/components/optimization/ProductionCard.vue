<template>
  <div
    class="prod-card"
    :class="item.canProduce ? 'can-produce' : 'cannot-produce'"
  >
    <div v-if="item.canProduce" class="prod-qty-badge">✦ {{ item.quantity }}x unidades</div>
    <div
      v-else
      class="prod-qty-badge"
      style="background: rgba(139,58,58,0.12); border-color: rgba(139,58,58,0.25); color: #e07070"
    >
      ✕ Não produzir
    </div>
    <div class="prod-name">{{ item.name }}</div>
    <div v-if="item.canProduce" class="prod-financials">
      <div class="prod-unit">
        Unitário<br /><strong>{{ formatCurrency(item.unitProfit) }}</strong>
      </div>
      <div class="prod-total">{{ formatCurrency(item.totalProfit) }}</div>
    </div>
    <div v-if="!item.canProduce && item.reason" class="prod-reason">
      {{ item.reason }}
    </div>
  </div>
</template>

<script setup>
/**
 * ProductionCard.vue — Card de um item do plano de produção
 *
 * Recebe do pai (DashboardView) apenas o que precisa para exibir:
 * - item: { id, name, quantity, unitProfit, totalProfit, canProduce, reason }
 * - formatCurrency: função para formatar valores em BRL
 *
 * Componente “burro”: só exibe; não emite eventos nem chama API.
 */

defineProps({
  item: {
    type: Object,
    required: true,
  },
  formatCurrency: {
    type: Function,
    required: true,
  },
});
</script>
