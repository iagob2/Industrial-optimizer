<template>
  <div class="cta-card" style="margin-bottom: 24px">
    <div class="card-label">Motor de Otimização · Greedy Algorithm</div>
    <div class="cta-headline">
      Maximize o <em>lucro</em><br />da produção hoje.
    </div>
    <p class="cta-sub">
      O algoritmo analisa o estoque atual de matérias-primas e calcula o plano de produção
      otimizado, resolvendo conflitos de insumos automaticamente.
    </p>
    <button
      type="button"
      class="cta-btn"
      :class="{ loading: loading }"
      :disabled="loading"
      @click="onClick"
    >
      <div v-if="loading" class="spinner"></div>
      <span v-else class="btn-icon">⬡</span>
      {{ loading ? 'Calculando...' : 'Otimizar Produção (Maximizar Lucros)' }}
    </button>
  </div>
</template>

<script setup>
/**
 * CtaCard.vue — Botão de ação "Otimizar"
 *
 * Não chama a API diretamente. Emite o evento 'optimize' para o pai (DashboardView).
 * O pai é responsável por chamar calculateOptimization() e passar loading via prop.
 *
 * Fluxo: usuário clica → emit('optimize') → DashboardView escuta @optimize → chama API
 */

defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['optimize']);

function onClick() {
  emit('optimize');
}
</script>
