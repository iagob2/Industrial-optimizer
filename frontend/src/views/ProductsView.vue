<template>
  <div class="page">
    <div class="table-header">
      <h1 class="page-title">Produtos</h1>
      <button type="button" class="add-btn" @click="openCreate">+ Novo Produto</button>
    </div>

    <div v-if="error" class="limitation-card">
      <div class="limitation-header">Erro</div>
      <div class="limitation-item">{{ error }}</div>
    </div>

    <div class="card" style="padding: 0">
      <table class="crud-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Código</th>
            <th>Nome</th>
            <th>Valor de Venda</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id">
            <td><span class="td-mono" style="color: var(--ash)">#{{ p.id }}</span></td>
            <td class="td-mono">{{ p.code }}</td>
            <td class="td-name">{{ p.name }}</td>
            <td class="td-value">{{ formatCurrency(p.saleValue) }}</td>
            <td>
              <button type="button" class="action-btn" @click="openEdit(p)" title="Editar">✎</button>
              <button type="button" class="action-btn danger" @click="confirmDelete(p)" title="Excluir">✕</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="products.length === 0 && !loading" style="padding: 24px; color: var(--ash);">Nenhum produto cadastrado.</p>
    </div>

    <!-- Modal Criar/Editar -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <h2 class="modal-title">{{ editingId ? 'Editar Produto' : 'Novo Produto' }}</h2>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Código</label>
            <input v-model="form.code" type="text" required placeholder="ex: PROD-001" />
          </div>
          <div class="form-group">
            <label>Nome</label>
            <input v-model="form.name" type="text" required placeholder="Nome do produto" />
          </div>
          <div class="form-group">
            <label>Valor de venda (R$)</label>
            <input v-model.number="form.saleValue" type="number" step="0.01" min="0" required placeholder="0.00" />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeModal">Cancelar</button>
            <button type="submit" class="btn-save">Salvar</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Confirmação de exclusão -->
    <div v-if="deleting" class="modal-overlay" @click.self="deleting = null">
      <div class="modal-box">
        <h2 class="modal-title">Excluir produto?</h2>
        <p style="color: var(--fog); margin-bottom: 24px;">{{ deleting.name }} — esta ação não pode ser desfeita.</p>
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="deleting = null">Cancelar</button>
          <button type="button" class="btn-save" style="background: var(--danger);" @click="doDelete">Excluir</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '../api/client';

const products = ref([]);
const loading = ref(false);
const error = ref(null);
const showModal = ref(false);
const editingId = ref(null);
const deleting = ref(null);

const form = ref({
  code: '',
  name: '',
  saleValue: 0,
});

function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v));
}

async function load() {
  error.value = null;
  loading.value = true;
  try {
    products.value = await api.getProducts();
  } catch (e) {
    error.value = e.message || 'Falha ao carregar produtos.';
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingId.value = null;
  form.value = { code: '', name: '', saleValue: 0 };
  showModal.value = true;
}

function openEdit(p) {
  editingId.value = p.id;
  form.value = { code: p.code, name: p.name, saleValue: Number(p.saleValue) };
  showModal.value = true;
}

function closeModal() {
  showModal.value = false;
  editingId.value = null;
}

async function save() {
  error.value = null;
  try {
    const body = {
      code: form.value.code,
      name: form.value.name,
      saleValue: form.value.saleValue,
    };
    if (editingId.value) {
      await api.updateProduct(editingId.value, body);
    } else {
      await api.createProduct(body);
    }
    closeModal();
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao salvar.';
  }
}

function confirmDelete(p) {
  deleting.value = p;
}

async function doDelete() {
  if (!deleting.value) return;
  error.value = null;
  try {
    await api.deleteProduct(deleting.value.id);
    deleting.value = null;
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao excluir.';
  }
}

onMounted(load);
</script>
