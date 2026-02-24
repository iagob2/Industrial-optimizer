<template>
  <div class="page">
    <div class="table-header">
      <h1 class="page-title">Composição dos Produtos</h1>
    </div>

    <div v-if="error" class="limitation-card">
      <div class="limitation-header">Erro</div>
      <div class="limitation-item">{{ error }}</div>
    </div>

    <div v-for="p in products" :key="p.id" class="card" style="margin-bottom: 16px">
      <div class="card-label" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
        <span>{{ p.name }} · {{ formatCurrency(p.saleValue) }}</span>
        <button type="button" class="add-btn" @click="openAdd(p)">+ Adicionar à receita</button>
      </div>
      <table class="crud-table">
        <thead>
          <tr>
            <th>Matéria-prima</th>
            <th>Quantidade necessária</th>
            <th>Unidade</th>
            <th>Custo no produto</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in (p.compositions || [])" :key="c.rawMaterial?.id ?? c.id?.rawMaterialId">
            <td class="td-name">{{ c.rawMaterial?.name ?? '—' }}</td>
            <td class="td-mono">{{ c.quantityNeeded }}</td>
            <td><span class="unit-pill">{{ c.rawMaterial?.unitMeasure ?? '—' }}</span></td>
            <td class="td-value" style="font-size: 14px">{{ formatCurrency(costInProduct(c)) }}</td>
            <td>
              <button type="button" class="action-btn" @click="openEdit(p, c)" title="Editar">✎</button>
              <button type="button" class="action-btn danger" @click="confirmDelete(p, c)" title="Excluir">✕</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="!p.compositions?.length" style="padding: 16px 0; color: var(--ash); font-size: 13px;">Nenhuma composição cadastrada. Use "Adicionar à receita" para criar.</p>
    </div>

    <p v-if="products.length === 0 && !loading" style="color: var(--ash);">Nenhum produto cadastrado. Cadastre um produto na aba Produtos primeiro.</p>

    <!-- Modal Nova composição -->
    <div v-if="showModal === 'add'" class="modal-overlay" @click.self="showModal = null">
      <div class="modal-box">
        <h2 class="modal-title">Adicionar à receita</h2>
        <p v-if="formProduct" style="color: var(--fog); margin-bottom: 16px; font-size: 13px;">Produto: <strong>{{ formProduct.name }}</strong></p>
        <form @submit.prevent="saveComposition">
          <div class="form-group">
            <label>Matéria-prima</label>
            <select v-model.number="form.rawMaterialId" required class="form-select">
              <option value="">Selecione o insumo</option>
              <option v-for="m in materials" :key="m.id" :value="m.id">{{ m.name }} ({{ m.unitMeasure }})</option>
            </select>
          </div>
          <div class="form-group">
            <label>Quantidade necessária</label>
            <input v-model.number="form.quantityNeeded" type="number" step="0.001" min="0.001" required placeholder="Ex: 2.5" />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showModal = null">Cancelar</button>
            <button type="submit" class="btn-save">Adicionar</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal Editar quantidade -->
    <div v-if="showModal === 'edit'" class="modal-overlay" @click.self="showModal = null">
      <div class="modal-box">
        <h2 class="modal-title">Editar quantidade</h2>
        <p style="color: var(--fog); margin-bottom: 16px; font-size: 13px;">
          {{ formProduct?.name }} · {{ formMaterial?.name }}
        </p>
        <form @submit.prevent="updateComposition">
          <div class="form-group">
            <label>Quantidade necessária</label>
            <input v-model.number="form.quantityNeeded" type="number" step="0.001" min="0.001" required />
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showModal = null">Cancelar</button>
            <button type="submit" class="btn-save">Salvar</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Confirmação exclusão -->
    <div v-if="deleting" class="modal-overlay" @click.self="deleting = null">
      <div class="modal-box">
        <h2 class="modal-title">Remover da receita?</h2>
        <p style="color: var(--fog); margin-bottom: 24px;">
          {{ deleting.productName }} — {{ deleting.materialName }}. Esta ação não pode ser desfeita.
        </p>
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="deleting = null">Cancelar</button>
          <button type="button" class="btn-save" style="background: var(--danger);" @click="doDelete">Remover</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '../api/client';

const products = ref([]);
const materials = ref([]);
const loading = ref(false);
const error = ref(null);
const showModal = ref(null); // 'add' | 'edit' | null
const deleting = ref(null);

const form = ref({
  productId: null,
  rawMaterialId: null,
  quantityNeeded: 0.001,
});

const formProduct = ref(null);
const formMaterial = ref(null);

function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v));
}

function costInProduct(comp) {
  const qty = Number(comp.quantityNeeded);
  const cost = comp.rawMaterial ? Number(comp.rawMaterial.unitCost) : 0;
  return qty * cost;
}

async function load() {
  error.value = null;
  loading.value = true;
  try {
    const [prods, mats] = await Promise.all([api.getProducts(), api.getMaterials()]);
    products.value = prods;
    materials.value = mats;
  } catch (e) {
    error.value = e.message || 'Falha ao carregar dados.';
  } finally {
    loading.value = false;
  }
}

function openAdd(product) {
  formProduct.value = product;
  form.value = { productId: product.id, rawMaterialId: '', quantityNeeded: 0.001 };
  showModal.value = 'add';
}

function openEdit(product, composition) {
  const rawMaterial = composition.rawMaterial;
  if (!rawMaterial) return;
  formProduct.value = product;
  formMaterial.value = rawMaterial;
  form.value = {
    productId: product.id,
    rawMaterialId: rawMaterial.id,
    quantityNeeded: Number(composition.quantityNeeded),
  };
  showModal.value = 'edit';
}

async function saveComposition() {
  error.value = null;
  try {
    await api.createComposition({
      productId: form.value.productId,
      rawMaterialId: form.value.rawMaterialId,
      quantityNeeded: form.value.quantityNeeded,
    });
    showModal.value = null;
    formProduct.value = null;
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao adicionar à receita.';
  }
}

async function updateComposition() {
  error.value = null;
  try {
    await api.updateComposition(form.value.productId, form.value.rawMaterialId, {
      quantityNeeded: form.value.quantityNeeded,
    });
    showModal.value = null;
    formProduct.value = null;
    formMaterial.value = null;
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao atualizar.';
  }
}

function confirmDelete(product, composition) {
  const rawMaterial = composition.rawMaterial;
  deleting.value = {
    productId: product.id,
    rawMaterialId: rawMaterial?.id,
    productName: product.name,
    materialName: rawMaterial?.name ?? '—',
  };
}

async function doDelete() {
  if (!deleting.value) return;
  error.value = null;
  try {
    await api.deleteComposition(deleting.value.productId, deleting.value.rawMaterialId);
    deleting.value = null;
    await load();
  } catch (e) {
    error.value = e.message || 'Falha ao remover.';
  }
}

onMounted(load);
</script>

<style scoped>
.form-select {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(200, 168, 130, 0.15);
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 14px;
  color: var(--linen);
  font-family: 'Sora', sans-serif;
}
.form-select:focus {
  outline: none;
  border-color: var(--oak);
}
.form-select option {
  background: var(--charcoal-mid);
  color: var(--linen);
}
</style>
