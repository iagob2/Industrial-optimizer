<template>
  <div class="page">
    <div class="table-header">
      <h1 class="page-title">Composição dos Produtos</h1>
    </div>

    <div v-if="error" class="limitation-card">
      <div class="limitation-header">Atenção</div>
      <div class="limitation-item">{{ error }}</div>
    </div>

    <div v-for="p in products" :key="p.id" class="card" style="margin-bottom: 24px">
      <div class="card-label" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
        <span style="font-size: 18px; color: var(--white)">
          {{ p.name }} <span style="color: var(--oak); margin: 0 8px">·</span> {{ formatCurrency(p.saleValue) }}
        </span>
        <button type="button" class="add-btn" @click="openAdd(p)">+ ADICIONAR À RECEITA</button>
      </div>

      <table class="crud-table" v-if="p.compositions && p.compositions.length">
        <thead>
          <tr>
            <th>MATÉRIA-PRIMA</th>
            <th>QTD. NECESSÁRIA</th>
            <th>UNIDADE</th>
            <th>CUSTO NO PRODUTO</th>
            <th style="text-align: right">AÇÕES</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in p.compositions" :key="c.rawMaterialId">
  <td class="td-name">{{ c.rawMaterialName }}</td>
  <td class="td-mono">{{ c.quantityNeeded }}</td>
  <td><span class="unit-pill">{{ c.rawMaterialUnitMeasure }}</span></td>
  <td class="td-value">{{ formatCurrency(c.totalCost) }}</td>


            <td style="text-align: right">
              <button type="button" class="action-btn" @click="openEdit(p, c)" title="Editar">✎</button>
              <button type="button" class="action-btn danger" @click="confirmDelete(p, c)" title="Excluir">✕</button>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-else style="padding: 24px 0; color: var(--ash); font-size: 14px; text-align: center; border: 1px dashed var(--charcoal-soft); border-radius: 8px;">
        Nenhuma composição cadastrada para este produto.
      </p>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-box">
        <h2 class="modal-title">{{ showModal === 'add' ? 'Adicionar à Receita' : 'Editar Quantidade' }}</h2>
        <p style="color: var(--fog); margin-bottom: 20px; font-size: 13px;">
          Produto: <strong style="color: var(--oak)">{{ formProduct?.name }}</strong>
          <span v-if="showModal === 'edit'"> <br> Insumo: <strong>{{ formMaterialName }}</strong></span>
        </p>

        <form @submit.prevent="showModal === 'add' ? saveComposition() : updateComposition()">
          <div class="form-group" v-if="showModal === 'add'">
            <label>SELECIONE O INSUMO</label>
            <select v-model.number="form.rawMaterialId" required class="form-select">
              <option value="" disabled>Escolha uma matéria-prima...</option>
              <option v-for="m in materials" :key="m.id" :value="m.id">
                {{ m.name }} ({{ m.unitMeasure }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>QUANTIDADE NECESSÁRIA</label>
            <input v-model.number="form.quantityNeeded" type="number" step="0.001" min="0.001" required placeholder="0.000" />
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeModal">CANCELAR</button>
            <button type="submit" class="btn-save">
              {{ showModal === 'add' ? 'ADICIONAR' : 'SALVAR ALTERAÇÕES' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="deleting" class="modal-overlay" @click.self="deleting = null">
      <div class="modal-box">
        <h2 class="modal-title">Remover Insumo?</h2>
        <p style="color: var(--fog); margin-bottom: 24px;">
          Deseja remover <strong>{{ deleting.materialName }}</strong> da receita de <strong>{{ deleting.productName }}</strong>?
        </p>
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="deleting = null">CANCELAR</button>
          <button type="button" class="btn-save" style="background: var(--danger);" @click="doDelete">REMOVER</button>
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
const showModal = ref(null);
const deleting = ref(null);
const formProduct = ref(null);
const formMaterialName = ref('');

const form = ref({
  productId: null,
  rawMaterialId: '',
  quantityNeeded: 1,
});

function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(v || 0));
}

async function load() {
  error.value = null;
  loading.value = true;
  try {
    const [prods, mats] = await Promise.all([api.getProducts(), api.getMaterials()]);
    
    // Busca composições via DTO para cada produto
    for (let p of prods) {
      p.compositions = await api.getComposition(p.id);
    }
    
    products.value = prods;
    materials.value = mats;
  } catch (e) {
    error.value = "Erro de conexão com o servidor. Verifique se o Backend está rodando na porta 8080.";
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function openAdd(product) {
  formProduct.value = product;
  form.value = { productId: product.id, rawMaterialId: '', quantityNeeded: 1 };
  showModal.value = 'add';
}

function openEdit(product, comp) {
  formProduct.value = product;
  formMaterialName.value = comp.rawMaterialName; // Usa o nome direto do DTO
  form.value = {
    productId: comp.productId,
    rawMaterialId: comp.rawMaterialId,
    quantityNeeded: comp.quantityNeeded
  };
  showModal.value = 'edit';
}

function closeModal() {
  showModal.value = null;
  formProduct.value = null;
  error.value = null;
}

async function saveComposition() {
  try {
    await api.createComposition(form.value);
    closeModal();
    await load();
  } catch (e) {
    error.value = e.message.includes('409') ? "Este insumo já faz parte desta receita." : e.message;
  }
}

async function updateComposition() {
  try {
    await api.updateComposition(form.value.productId, form.value.rawMaterialId, {
      quantityNeeded: form.value.quantityNeeded
    });
    closeModal();
    await load();
  } catch (e) {
    error.value = "Erro ao atualizar quantidade.";
  }
}

function confirmDelete(product, comp) {
  deleting.value = {
    productId: comp.productId,
    rawMaterialId: comp.rawMaterialId,
    productName: product.name,
    materialName: comp.rawMaterialName // Usa o nome direto do DTO
  };
}

async function doDelete() {
  try {
    await api.deleteComposition(deleting.value.productId, deleting.value.rawMaterialId);
    deleting.value = null;
    await load();
  } catch (e) {
    error.value = "Erro ao remover item.";
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
  cursor: pointer;
}
.form-select option {
  background: #2c2c2c;
  color: white;
}
.unit-pill {
  background: var(--charcoal-soft);
  padding: 2px 8px;
  border-radius: 4px;
  font-family: 'DM Mono', monospace;
  font-size: 11px;
  color: var(--fog);
}
</style>