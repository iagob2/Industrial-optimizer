import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount, flushPromises } from '@vue/test-utils';
import DashboardView from '../views/DashboardView.vue';

// Mock da API usada pelo Dashboard (Optimization Panel)
vi.mock('../api/client', () => {
  const api = {
    getMaterials: vi.fn(),
    getProducts: vi.fn(),
    getOptimization: vi.fn(),
    getComposition: vi.fn(),
  };
  return { api };
});

import { api } from '../api/client';

const mockMaterials = [
  { id: 1, name: 'Tábua de Carvalho', stockQuantity: 50, unitMeasure: 'unit', unitCost: 45 },
];

const mockProducts = [
  { id: 1, name: 'Mesa de Escritório Luxo', saleValue: 1200.0 },
];

function createDeferred<T>() {
  let resolve!: (value: T) => void;
  let reject!: (reason?: unknown) => void;
  const promise = new Promise<T>((res, rej) => {
    resolve = res;
    reject = rej;
  });
  return { promise, resolve, reject };
}

describe('OptimizationPanel (DashboardView)', () => {
  beforeEach(() => {
    vi.resetAllMocks();
    (api.getMaterials as any).mockResolvedValue(mockMaterials);
    (api.getProducts as any).mockResolvedValue(mockProducts);
    (api.getComposition as any).mockResolvedValue([
      {
        productId: 1,
        rawMaterialId: 1,
        rawMaterialName: 'Tábua de Carvalho',
        rawMaterialUnitMeasure: 'unit',
        rawMaterialUnitCost: 45,
        quantityNeeded: 4,
        totalCost: 180,
      },
    ]);
  });

  // 1. Loading State: botão desabilita e mostra "Calculando..."
  it('desabilita o botão e mostra loading enquanto calcula', async () => {
    const deferred = createDeferred<Record<string, number>>();
    (api.getOptimization as any).mockReturnValue(deferred.promise);

    const wrapper = mount(DashboardView);
    await flushPromises();

    const button = wrapper.get('button.cta-btn');
    expect(button.attributes('disabled')).toBeUndefined();

    await button.trigger('click');

    // Enquanto a promise não é resolvida, deve estar em loading
    expect(button.attributes('disabled')).toBeDefined();
    expect(button.text()).toContain('Calculando...');

    deferred.resolve({ 'Mesa de Escritório Luxo': 1 });
    await flushPromises();

    expect(button.attributes('disabled')).toBeUndefined();
    expect(button.text()).toContain('Otimizar Produção');
  });

  // 2. Currency Formatting: lucro total em BRL (ex: "R$ 1.200,00")
  it('renderiza o lucro total formatado em BRL', async () => {
    (api.getOptimization as any).mockResolvedValue({ 'Mesa de Escritório Luxo': 1 });

    const wrapper = mount(DashboardView);
    await flushPromises();

    const button = wrapper.get('button.cta-btn');
    await button.trigger('click');
    await flushPromises();

    const profit = wrapper.get('.profit-value');
    expect(profit.text()).toContain('R$');
    expect(profit.text()).toContain('1.200,00');
  });

  // 3. Responsiveness: verifica classe Tailwind md:grid-cols-2 no container
  it('aplica classes responsivas no grid de produção', async () => {
    (api.getOptimization as any).mockResolvedValue({ 'Mesa de Escritório Luxo': 1 });

    const wrapper = mount(DashboardView);
    await flushPromises();

    const grids = wrapper.findAll('.production-grid');
    expect(grids.length).toBeGreaterThan(0);
    expect(grids[0].classes()).toContain('md:grid-cols-2');
  });

  // 4. Error Feedback: falha na API mostra mensagem clara
  it('exibe mensagem de erro quando a API de otimização falha', async () => {
    (api.getOptimization as any).mockRejectedValue(new Error('500 Internal Server Error'));

    const wrapper = mount(DashboardView);
    await flushPromises();

    const button = wrapper.get('button.cta-btn');
    await button.trigger('click');
    await flushPromises();

    const alert = wrapper.get('.limitation-card');
    expect(alert.text()).toMatch(/Erro na API|Erro ao calcular otimização/i);
  });
}

