<template>
  <div class="page">
    <div class="table-header">
      <h1 class="page-title">Insumos</h1>
      <button type="button" class="add-btn" @click="openCreate">+ Novo Insumo</button>
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
            <th>Estoque</th>
            <th>Unidade</th>
            <th>Custo Unit.</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in materials" :key="m.id">
            <td><span class="td-mono" style="color: var(--ash)">#{{ m.id }}</span></td>
            <td class="td-mono">{{ m.code }}</td>
            <td class="td-name">{{ m.name }}</td>
            <td class="td-mono">{{ m.stockQuantity }}</td>
            <td><span class="unit-pill">{{ m.unitMeasure }}</span></td>
            <td class="td-value" style="font-size: 14px">{{ formatCurrency(m.unitCost) }}</td>
            <td>
              <button type="button" class="action-btn" @click="openEdit(m)" title="Editar">✎</button>
              <button type="button" class="action-btn danger" @click="confirmDelete(m)" title="Excluir">✕</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="materials.length === 0 && !loading" style="padding: 24px; color: var(--ash);">Nenhum insumo cadastrado.</p>
    </div>

    <!-- Modal Criar/Editar -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <h2 class="modal-title">{{ editingId ? 'Editar Insumo' : 'Novo Insumo' }}</h2>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Código</label>
            <input v-model="form.code" type="text" required placeholder="ex: WOOD-01" />
          </div>
          <div class="form-group">
            <label>Nome</label>
            <input v-model="form.name" type="text" required placeholder="Nome do insumo" />
          </div>
          <div class="form-group">
            <label>Quantidade em estoque</label>
            <input v-model.number="form.stockQuantity" type="number" step="0.001" min="0" required placeholder="0" />
          </div>
          <div class="form-group">
            <label>Unidade de medida</label>
            <input v-model="form.unitMeasure" type="text" required placeholder="ex: unit, kg, m, L" />
          </div>
          <div class="form-group">
            <label>Custo unitário (R$)</label>
            <input v-model.number="form.unitCost" type="number" step="0.01" min="0" required placeholder="0.00" />
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
        <h2 class="modal-title">Excluir insumo?</h2>
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

const materials = ref([]);
const loading = ref(false);
const error = ref(null);
const showModal = ref(false);
const editingId = ref(null);
const deleting = ref(null);

const form = ref({
  code: '',
  name: '',
  stockQuantity: 0,
  unitMeasure: 'unit',
  unitCost: 0,
});

function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v));
}

async function load() {
  error.value = null;
  loading.value = true;
  try {
    materials.value = await api.getMaterials();
  } catch (e) {
    error.value = e.message || 'Falha ao carregar insumos.';
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingId.value = null;
  form.value = { code: '', name: '', stockQuantity: 0, unitMeasure: 'unit', unitCost: 0 };
  showModal.value = true;
}

function openEdit(m) {
  editingId.value = m.id;
  form.value = {
    code: m.code,
    name: m.name,
    stockQuantity: Number(m.stockQuantity),
    unitMeasure: m.unitMeasure,
    unitCost: Number(m.unitCost),
  };
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
      stockQuantity: form.value.stockQuantity,
      unitMeasure: form.value.unitMeasure,
      unitCost: form.value.unitCost,
    };
    if (editingId.value) {
      await api.updateMaterial(editingId.value, body);
    } else {
      await api.createMaterial(body);
    }
    closeModal();
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao salvar.';
  }
}

function confirmDelete(m) {
  deleting.value = m;
}

async function doDelete() {
  if (!deleting.value) return;
  error.value = null;
  try {
    await api.deleteMaterial(deleting.value.id);
    deleting.value = null;
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao excluir.';
  }
}

onMounted(load);
</script>
