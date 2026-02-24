<template>
  <div class="limitation-card">
    <div class="limitation-header">⚠ Limitações de Estoque ({{ items.length }})</div>
    <div v-for="(lim, i) in items" :key="i" class="limitation-item">{{ lim }}</div>
  </div>
</template>

<script setup>
defineProps({
  items: {
    type: Array,
    default: () => [],
  },
});
</script>
