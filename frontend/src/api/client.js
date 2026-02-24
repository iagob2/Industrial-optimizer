/**
 * Cliente HTTP para o backend Industrial Optimizer.
 * Em dev com Vite: use '' para aproveitar o proxy (evita CORS).
 * Em produção: defina VITE_API_URL (ex: https://api.seudominio.com) ou use proxy no servidor.
 */
const BASE_URL = import.meta.env.VITE_API_URL ?? '';

export async function fetchJson(path, options = {}) {
  const url = `${BASE_URL}${path}`;
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  if (!res.ok) throw new Error(`API ${res.status}: ${res.statusText}`);
  const text = await res.text();
  if (!text || text.trim() === '') throw new Error('Resposta da API vazia');
  try {
    return JSON.parse(text);
  } catch {
    throw new Error('Resposta da API inválida (JSON). Backend pode ter referência circular ou erro de serialização.');
  }
}

async function fetchWithMethod(path, method, body) {
  const url = `${BASE_URL}${path}`;
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  if (!res.ok) throw new Error(text || `API ${res.status}: ${res.statusText}`);
  if (!text || text.trim() === '') return null;
  try {
    return JSON.parse(text);
  } catch {
    throw new Error('Resposta da API inválida (JSON).');
  }
}

export const api = {
  getMaterials: () => fetchJson('/api/raw-materials'),
  getProducts: () => fetchJson('/api/products'),
  getOptimization: () => fetchJson('/api/products/suggest'),
  getProduct: (id) => fetchJson(`/api/products/${id}`),
  createProduct: (body) => fetchWithMethod('/api/products', 'POST', body),
  updateProduct: (id, body) => fetchWithMethod(`/api/products/${id}`, 'PUT', body),
  deleteProduct: (id) => fetchWithMethod(`/api/products/${id}`, 'DELETE'),
  getMaterial: (id) => fetchJson(`/api/raw-materials/${id}`),
  createMaterial: (body) => fetchWithMethod('/api/raw-materials', 'POST', body),
  updateMaterial: (id, body) => fetchWithMethod(`/api/raw-materials/${id}`, 'PUT', body),
  deleteMaterial: (id) => fetchWithMethod(`/api/raw-materials/${id}`, 'DELETE'),
  // DTO (achatado) — precisa enviar productId via query string
  getComposition: (productId) => fetchJson('/api/product-compositions?productId=' + productId),
  // Mantido por compatibilidade interna (se algum lugar ainda chamar no plural)
  getCompositions: (productId) => fetchJson('/api/product-compositions?productId=' + productId),
  createComposition: (body) => fetchWithMethod('/api/product-compositions', 'POST', body),
  updateComposition: (productId, rawMaterialId, body) =>
    fetchWithMethod(`/api/product-compositions/${productId}/${rawMaterialId}`, 'PUT', body),
  deleteComposition: (productId, rawMaterialId) =>
    fetchWithMethod(`/api/product-compositions/${productId}/${rawMaterialId}`, 'DELETE'),
};


