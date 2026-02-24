<template>
  <div id="app">
    <aside class="sidebar">
      <div class="sidebar-logo">IO</div>
      <button
        v-for="t in tabs"
        :key="t.id"
        type="button"
        class="nav-item"
        :class="{ active: activeTab === t.id }"
        @click="activeTab = t.id"
      >
        {{ t.icon }}
        <span class="nav-tooltip">{{ t.label }}</span>
      </button>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="topbar-title">Industrial <span>Optimizer</span></div>
        <div class="topbar-meta">Fábrica de Móveis · {{ currentDate }}</div>
      </header>

      <nav class="tabs">
        <button
          v-for="t in tabs"
          :key="t.id"
          type="button"
          class="tab"
          :class="{ active: activeTab === t.id }"
          @click="activeTab = t.id"
        >
          {{ t.tabLabel }}
        </button>
      </nav>

      <DashboardView v-if="activeTab === 'dashboard'" />
      <ProductsView v-else-if="activeTab === 'products'" />
      <MaterialsView v-else-if="activeTab === 'materials'" />
      <CompositionView v-else-if="activeTab === 'recipes'" />
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import DashboardView from './views/DashboardView.vue';
import ProductsView from './views/ProductsView.vue';
import MaterialsView from './views/MaterialsView.vue';
import CompositionView from './views/CompositionView.vue';

const activeTab = ref('dashboard');

const tabs = [
  { id: 'dashboard', icon: '⬡', label: 'Dashboard', tabLabel: 'Otimização' },
  { id: 'products', icon: '◻', label: 'Produtos', tabLabel: 'Produtos' },
  { id: 'materials', icon: '◈', label: 'Insumos', tabLabel: 'Insumos' },
  { id: 'recipes', icon: '⊞', label: 'Composição', tabLabel: 'Composição' },
];

const currentDate = computed(() =>
  new Date().toLocaleDateString('pt-BR', {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  })
);
</script>
