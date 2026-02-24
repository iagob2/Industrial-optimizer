# 📚 Documentação Completa — Industrial Optimizer Frontend
> Arquivo de referência total: de onde veio cada decisão, estilo, biblioteca e técnica usada.

---

## 📋 Índice

1. [O Estilo — O que é "Industrial Escandinavo"?](#1-o-estilo)
2. [Tipografia — As 3 Fontes e Por Quê](#2-tipografia)
3. [Paleta de Cores — Cada Cor Explicada](#3-paleta-de-cores)
4. [Bibliotecas e CDNs Usados](#4-bibliotecas-e-cdns)
5. [Técnicas de CSS Avançado](#5-técnicas-de-css-avançado)
6. [Layout e Estrutura](#6-layout-e-estrutura)
7. [Animações e Micro-interações](#7-animações-e-micro-interações)
8. [Vue.js 3 — O que foi usado](#8-vuejs-3)
9. [Sites de Referência e Inspiração](#9-sites-de-referência)
10. [Glossário de Termos de Design](#10-glossário)

---

## 1. O Estilo

### Nome: **"Dark Industrial Escandinavo"**

Este estilo é uma fusão de dois movimentos de design:

### 🇸🇪 Design Escandinavo (Scandinavian Design)
- Origem: **Países Nórdicos (Suécia, Dinamarca, Finlândia)** — movimento ativo desde os anos 1950
- Princípios: **Funcionalidade acima de tudo**, sem ornamentos desnecessários, espaço em branco generoso, materiais naturais (madeira, linho, couro)
- No digital: layouts limpos, tipografia com personalidade, hierarquia clara, poucos elementos mas cada um com propósito
- Referência histórica: **IKEA**, **Bang & Olufsen**, **Muuto** (móveis), **Figma** (produto digital com raízes nórdicas)

### 🏭 Aesthetic Industrial
- Origem: **Estética de fábricas e armazéns** — popularizada no Brooklyn, NY nos anos 2000
- Características: tons escuros, carvão, metal, concreto, madeira bruta, tipografia monospace (tipo terminais)
- No digital: fundos escuros (dark mode), bordas metálicas finas, labels em caps-lock com letra-spacing, fontes de terminal

### 🔀 A Fusão dos Dois
A combinação cria um dashboard que parece:
- Uma **interface de controle industrial premium** (pense: painéis de monitoramento de fábricas reais, como Siemens ou Rockwell Automation)
- Com o **refinamento e calma** do design escandinavo (nada grita, tudo respira)
- **Tom amadeirado (carvalho)** como cor de destaque — referência direta ao produto da fábrica (móveis de madeira)

### Sites que usam estilo parecido:
- [Linear.app](https://linear.app) — dark + minimalista, ferramenta de gestão
- [Vercel Dashboard](https://vercel.com/dashboard) — dark industrial para devs
- [Resend.com](https://resend.com) — dark com tipografia serif + mono
- [Railway.app](https://railway.app) — dark com acento colorido específico
- [Basement.studio](https://basement.studio) — referência máxima de dark industrial criativo

---

## 2. Tipografia

Três fontes foram escolhidas com papéis distintos. Todas vêm do **Google Fonts** (gratuitas).

### 🔤 Fonte 1: `DM Serif Display`
- **Papel:** Títulos, números grandes, logo, destaque emocional
- **Categoria:** Serif (com serifa — aquelas "pernas" nas letras)
- **Por que foi escolhida:**
  - Tem personalidade editorial, parece saída de uma revista de arquitetura ou móveis de luxo
  - O contraste entre a "frieza" do dark mode e a "elegância" de uma serif cria tensão visual interessante
  - Suporta **itálico** — usado no headline "Maximize o *lucro*" para dar drama
- **Onde está no código:** `font-family: 'DM Serif Display', serif`
- **Link Google Fonts:** https://fonts.google.com/specimen/DM+Serif+Display
- **Criada por:** Colophon Foundry para o Google

### 🔤 Fonte 2: `DM Mono`
- **Papel:** Labels técnicos, IDs, valores de código, metadados, badges
- **Categoria:** Monospace (cada letra tem a mesma largura, como em terminais de computador)
- **Por que foi escolhida:**
  - Dá sensação de **precisão técnica** — como se fossem dados vindos de um sistema real
  - Perfeita para `#001`, `120 m`, `R$ 1.890,00` — coisas que precisam parecer "dados de banco"
  - É da mesma família DM (Design Micro), então combina visualmente com a DM Serif
- **Onde está no código:** `font-family: 'DM Mono', monospace`
- **Link Google Fonts:** https://fonts.google.com/specimen/DM+Mono
- **Criada por:** Colophon Foundry para o Google

### 🔤 Fonte 3: `Sora`
- **Papel:** Corpo de texto, parágrafos, botões, navegação, texto geral
- **Categoria:** Sans-serif geométrica
- **Por que foi escolhida:**
  - Legível em tamanhos pequenos (12px, 11px) sem perder clareza
  - Tem traços levemente geométricos que combinam com o aesthetic industrial
  - É "neutra o suficiente" para não competir com as duas fontes de personalidade acima
- **Onde está no código:** `font-family: 'Sora', sans-serif` (no body)
- **Link Google Fonts:** https://fonts.google.com/specimen/Sora
- **Criada por:** Satoshi Kono

### Como importar as fontes (linha 7 do HTML):
```html
<link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Mono:wght@300;400;500&family=Sora:wght@300;400;500;600&display=swap" rel="stylesheet" />
```

**Explicação dos parâmetros:**
- `ital@0;1` → importa o estilo normal (0) E itálico (1) da DM Serif
- `wght@300;400;500` → importa os pesos Light, Regular e Medium da DM Mono
- `wght@300;400;500;600` → importa Light, Regular, Medium e SemiBold da Sora
- `display=swap` → performance: exibe a fonte do sistema enquanto carrega a custom

---

## 3. Paleta de Cores

Todas as cores estão definidas como **CSS Custom Properties** (variáveis CSS) no `:root`. Isso significa que mudar uma variável altera o tema inteiro.

```css
:root {
  /* ── TONS DE CARVALHO (Oak) — a cor de destaque */
  --oak:       #C8A882;  /* Carvalho médio — cor principal de ação */
  --oak-dark:  #9C7A55;  /* Carvalho escuro — hover dos botões */
  --oak-light: #E8D5B9;  /* Carvalho claro — backgrounds suaves */

  /* ── TONS DE CARVÃO (Charcoal) — backgrounds */
  --charcoal:      #1E1E1E;  /* Fundo principal — quase preto */
  --charcoal-mid:  #2C2C2C;  /* Cards e sidebar — um tom mais claro */
  --charcoal-soft: #3A3A3A;  /* Tooltips — o mais claro dos escuros */

  /* ── TONS DE CINZA NEUTRO — texto secundário */
  --ash:  #6B6B6B;  /* Texto desativado, ícones inativos */
  --fog:  #B0A99A;  /* Texto secundário, subtítulos — tem um leve tom bege */

  /* ── TONS DE LINHO (Linen) — texto principal */
  --linen:      #F5F0E8;  /* Texto principal — não é branco puro, tem calor */
  --linen-dark: #EDE5D8;  /* Variação mais escura do linho */
  --white:      #FAFAF8;  /* "Branco" do título — ainda levemente quente */

  /* ── CORES SEMÂNTICAS — feedback de sistema */
  --success: #4A7C59;  /* Verde escuro — estoque OK */
  --warning: #B8860B;  /* Âmbar — estoque em alerta */
  --danger:  #8B3A3A;  /* Vermelho escuro — estoque crítico */
}
```

### Por que não usar branco puro (#FFFFFF)?
Branco puro em fundo preto puro (#000000) cria **contraste excessivo** que cansa os olhos. A combinação `#FAFAF8` (quase branco levemente quente) em `#1E1E1E` (carvão quente) é mais confortável para longos períodos de uso — técnica usada pelo Linear, Vercel e Notion.

### Por que o tom "Oak" (carvalho) como destaque?
- **Coerência narrativa:** A fábrica produz móveis de madeira. A cor de destaque da interface é literalmente a cor da madeira de carvalho. O design conta a história do produto.
- **Originalidade:** 99% dos dashboards usam azul ou roxo como destaque. Carvalho diferencia imediatamente.
- **Temperatura:** Tons quentes (marrom, bege, âmbar) criam sensação de solidez e confiança — ideal para um sistema industrial.

---

## 4. Bibliotecas e CDNs

### Vue.js 3.4.21
- **O que é:** Framework JavaScript progressivo para construir interfaces reativas
- **Por que essa versão:** 3.4.x é a versão LTS (Long Term Support) mais estável no momento da criação
- **CDN usado:**
  ```html
  <script src="https://cdnjs.cloudflare.com/ajax/libs/vue/3.4.21/vue.global.prod.min.js"></script>
  ```
- **URL do CDN:** https://cdnjs.cloudflare.com/ajax/libs/vue/3.4.21/vue.global.prod.min.js
- **Site oficial:** https://vuejs.org
- **Documentação:** https://vuejs.org/guide/introduction

> **Nota importante:** Em um projeto Vue real com Vite, você não usaria esse CDN. Você instalaria via `npm install vue` e usaria arquivos `.vue` com `<script setup>`. O CDN foi usado aqui para permitir rodar o arquivo HTML diretamente no browser sem build.

### Google Fonts
- **O que é:** CDN gratuito de fontes web do Google
- **URL:** https://fonts.googleapis.com
- **Site:** https://fonts.google.com
- **Como funciona:** Você monta a URL com os parâmetros das fontes que quer e inclui como `<link>` no `<head>`. O browser baixa apenas os pesos que você pediu.

### cdnjs.cloudflare.com
- **O que é:** CDN (Content Delivery Network) da Cloudflare com bibliotecas JavaScript open-source
- **Site:** https://cdnjs.com
- **Por que usar:** Servidor distribuído globalmente — carrega mais rápido que baixar do npm em demos e protótipos

---

## 5. Técnicas de CSS Avançado

### 5.1 Grain Overlay (Textura de Grão)
```css
body::before {
  content: '';
  position: fixed;
  inset: 0;
  background-image: url("data:image/svg+xml,...feTurbulence...");
  pointer-events: none;
  z-index: 9999;
  opacity: 0.4;
}
```
**O que faz:** Sobrepõe uma textura de "grão de papel/película" em toda a tela.

**Como funciona:**
- Usa um SVG inline codificado em base64 dentro de um `data:` URI
- O SVG contém um filtro `feTurbulence` com `fractalNoise` — que é um gerador de ruído matemático (Perlin Noise)
- `pointer-events: none` garante que o overlay não interfere com cliques
- `z-index: 9999` garante que fica na frente de tudo
- `position: fixed` garante que não rola com a página

**De onde vem essa técnica:**
- Muito usada por sites de design premium como Awwwards.com e agências criativas
- Inspiração direta: [Basement Studio](https://basement.studio), [Rauno.me](https://rauno.me)
- O filtro SVG `feTurbulence` é especificado pela W3C: https://www.w3.org/TR/filter-effects/#feTurbulenceElement

### 5.2 CSS Custom Properties (Variáveis CSS)
```css
:root { --oak: #C8A882; }
.button { background: var(--oak); }
```
**Documentação MDN:** https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties

**Por que usar:** Muda toda a paleta em um único lugar. Para criar um tema claro, basta sobrescrever as variáveis em uma media query `@prefers-color-scheme: light`.

### 5.3 `backdrop-filter: blur()`
```css
.topbar {
  background: rgba(30,30,30,0.8);
  backdrop-filter: blur(12px);
}
```
**O que faz:** O efeito "vidro fosco" (frosted glass) popularizado pelo macOS. O conteúdo atrás da topbar fica visível mas desfocado.

**Documentação MDN:** https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter

**Suporte:** Funciona em todos browsers modernos. No Firefox desktop, pode precisar de flag. Safari suporta com `-webkit-backdrop-filter`.

### 5.4 `inset: 0` (Shorthand de posicionamento)
```css
position: fixed;
inset: 0; /* equivale a: top:0; right:0; bottom:0; left:0; */
```
**Documentação MDN:** https://developer.mozilla.org/en-US/docs/Web/CSS/inset

### 5.5 `::before` e `::after` para decoração
```css
.cta-card::after {
  content: 'IO';
  position: absolute;
  font-size: 140px;
  color: rgba(200,168,130,0.04); /* quase invisível */
}
```
**O que faz:** Adiciona elementos decorativos (a marca d'água "IO" gigante no card) sem poluir o HTML.

**Regra:** Todo elemento com `::before` ou `::after` precisa de `position: relative` no pai e `position: absolute` no pseudo-elemento.

### 5.6 Animações com `@keyframes`
```css
@keyframes slideUp {
  from { opacity: 0; transform: translateY(24px); }
  to   { opacity: 1; transform: translateY(0); }
}

.result-panel {
  animation: slideUp 0.5s cubic-bezier(0.4,0,0.2,1);
}
```

**`cubic-bezier(0.4,0,0.2,1)`** — este é o easing "Material Design Decelerate" do Google. Faz a animação começar rápida e desacelerar no final, imitando física real.
- Ferramenta para visualizar beziers: https://cubic-bezier.com

### 5.7 Animação com `animation-delay` (Stagger)
```css
.prod-card:nth-child(1) { animation-delay: 0.05s; }
.prod-card:nth-child(2) { animation-delay: 0.10s; }
.prod-card:nth-child(3) { animation-delay: 0.15s; }
```
**O que faz:** Cada card aparece 50ms depois do anterior — criando o efeito "cascata" que dá sensação de movimento vivo.

**Técnica chamada:** "Staggered animation" ou "Sequential reveal"

### 5.8 CSS Grid
```css
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 380px; /* coluna flexível + coluna fixa */
  gap: 24px;
}
```
**Documentação MDN:** https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout

**`1fr`** = "1 fraction" — ocupa todo espaço disponível restante.

---

## 6. Layout e Estrutura

### Padrão: "Shell Layout" (Sidebar Fixa + Conteúdo Principal)
```
┌──────┬────────────────────────────────────┐
│      │  TOPBAR (sticky)                   │
│ SIDE │────────────────────────────────────│
│ BAR  │  TABS                              │
│      │────────────────────────────────────│
│ 72px │  CONTEÚDO DA PÁGINA                │
│      │  (scrollável)                      │
└──────┴────────────────────────────────────┘
```

- Sidebar: `position: fixed` — não rola com a página
- Conteúdo: `margin-left: 72px` — recua para não ficar atrás da sidebar
- Topbar: `position: sticky; top: 0` — gruda no topo ao scrollar

**Esse padrão de layout é usado por:** Linear, Notion, Figma, GitHub, Vercel, Jira

### Por que sidebar de ícones (72px) e não sidebar com texto?
- Em telas menores, economiza espaço horizontal
- Força o usuário a aprender os ícones — mais rápido no uso contínuo
- Tooltips aparecem no hover para ensinar o usuário inicialmente
- Referência: [Linear App Navigation](https://linear.app)

---

## 7. Animações e Micro-interações

| Elemento | Animação | Duração | Técnica |
|---|---|---|---|
| Troca de tab | `fadeIn` — opacidade + translateY | 0.4s | `@keyframes` + `animation` |
| Painel de resultado | `slideUp` — opacidade + translateY maior | 0.5s | `@keyframes` com cubic-bezier |
| Cards de produção | `cardIn` — escalonado por nth-child | 0.4s + delay | Stagger animation |
| Botão CTA (hover) | Sobe 2px + sombra dourada | 0.25s | `transform: translateY(-2px)` + `box-shadow` |
| Spinner de loading | Rotação contínua | 0.7s infinito | `@keyframes spin` |
| Barras de progresso | Expansão da largura | 0.8s / 1s | `transition: width` |
| Itens de nav (hover) | Background fade + mudança de cor | 0.2s | `transition: all` |
| Tooltips da sidebar | Opacidade fade | 0.15s | `transition: opacity` |

---

## 8. Vue.js 3

### Composition API com `<script setup>`
O projeto usa a **Composition API** do Vue 3, que é a forma moderna de escrever componentes Vue.

```javascript
const { createApp, ref, computed } = Vue;

createApp({
  setup() {
    // Toda a lógica fica aqui
    const activeTab = ref('dashboard'); // dado reativo
    const criticalCount = computed(() => ...); // valor calculado
    return { activeTab, criticalCount, ... }; // expõe para o template
  }
}).mount('#app');
```

**Documentação oficial:** https://vuejs.org/guide/extras/composition-api-faq

### `ref()` — Dados Reativos
```javascript
const isLoading = ref(false);
isLoading.value = true; // modifica o valor
```
**O que faz:** Cria um valor reativo. Quando ele muda, o Vue atualiza automaticamente o HTML que o usa.

**Documentação:** https://vuejs.org/api/reactivity-core#ref

### `computed()` — Valores Calculados
```javascript
const criticalCount = computed(() =>
  materials.value.filter(m => getStockPct(m) < 50).length
);
```
**O que faz:** Calcula um valor derivado de outros dados reativos. Só recalcula quando os dados de origem mudam — é cacheado automaticamente.

**Documentação:** https://vuejs.org/api/reactivity-core#computed

### Diretivas Vue usadas no template

| Diretiva | O que faz | Exemplo no código |
|---|---|---|
| `v-if` | Renderiza condicionalmente | `v-if="activeTab === 'dashboard'"` |
| `v-for` | Loop para renderizar listas | `v-for="m in materials" :key="m.id"` |
| `v-bind` (`:`) | Vincula atributo dinamicamente | `:class="{ active: isLoading }"` |
| `v-on` (`@`) | Escuta eventos | `@click="runOptimization"` |
| `:disabled` | Desabilita botão reativamente | `:disabled="isLoading"` |

**Documentação diretivas:** https://vuejs.org/api/built-in-directives

### `Intl.NumberFormat` — Formatação de Moeda
```javascript
function formatCurrency(v) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(v);
}
```
**O que é:** API nativa do JavaScript para formatação de números internacionalizados. Não precisa de biblioteca externa.

**Documentação MDN:** https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/NumberFormat

---

## 9. Sites de Referência e Inspiração

### Dashboards que inspiraram o layout:

| Site | O que foi inspirado | URL |
|---|---|---|
| **Linear App** | Sidebar de ícones, dark theme refinado, navegação por tabs | https://linear.app |
| **Vercel Dashboard** | Topbar sticky com backdrop blur, cards de status, tabelas | https://vercel.com/dashboard |
| **Resend.com** | Combinação serif + mono, dark com acento específico | https://resend.com |
| **Basement Studio** | Grain overlay, tipografia dramática, estética industrial | https://basement.studio |
| **Rauno.me** | Detalhes de micro-interação, uso de DM Serif | https://rauno.me |

### Ferramentas de Design usadas como referência:

| Ferramenta | Para o quê | URL |
|---|---|---|
| **Coolors.co** | Explorar paletas de cor | https://coolors.co |
| **Google Fonts** | Pesquisar e comparar fontes | https://fonts.google.com |
| **cubic-bezier.com** | Visualizar curvas de animação | https://cubic-bezier.com |
| **CSS Gradient** | Construir gradientes CSS | https://cssgradient.io |
| **Awwwards** | Referência de design web premium | https://awwwards.com |
| **Dribbble** | Referências de UI de dashboards | https://dribbble.com/search/dashboard+dark |

### Documentações técnicas essenciais:

| Documentação | URL |
|---|---|
| Vue.js 3 (oficial) | https://vuejs.org/guide/introduction |
| MDN Web Docs (CSS) | https://developer.mozilla.org/en-US/docs/Web/CSS |
| CSS Tricks (Grid) | https://css-tricks.com/snippets/css/complete-guide-grid |
| CSS Tricks (Flexbox) | https://css-tricks.com/snippets/css/a-guide-to-flexbox |
| Can I Use (suporte browsers) | https://caniuse.com |
| SVG Filter Effects (W3C) | https://www.w3.org/TR/filter-effects |

---

## 10. Glossário

| Termo | Explicado em português |
|---|---|
| **CDN** | Servidor distribuído globalmente que entrega arquivos (como bibliotecas JS e fontes) de forma rápida. Ex: cdnjs, jsDelivr, Google Fonts |
| **CSS Custom Property** | Variável definida em CSS com `--nome: valor` e usada com `var(--nome)`. Permite temas dinâmicos |
| **Composition API** | Forma de escrever componentes Vue 3 usando funções `ref`, `computed`, `setup()` em vez de Options API (data, methods, computed) |
| **Reactive Data** | Dado que, quando muda, automaticamente atualiza o HTML que o utiliza |
| **`ref()`** | Função Vue que torna um valor primitivo reativo |
| **`computed()`** | Função Vue que cria um valor derivado, calculado apenas quando necessário |
| **Pseudo-elemento** | `::before` e `::after` — elementos virtuais criados pelo CSS sem HTML adicional |
| **`backdrop-filter`** | Propriedade CSS que aplica efeito visual no conteúdo *atrás* de um elemento (ex: blur) |
| **Grain Overlay** | Textura de ruído/grão aplicada sobre a interface para dar profundidade e sensação de material |
| **Stagger Animation** | Animações em sequência com atraso entre cada elemento, criando efeito "cascata" |
| **Frosted Glass** | Efeito "vidro fosco" — background semitransparente com blur. Popularizado pelo macOS Big Sur |
| **`feTurbulence`** | Filtro SVG que gera padrões de ruído matemático (Perlin Noise). Usado para criar o grain |
| **`cubic-bezier`** | Função matemática que define a curva de aceleração de uma animação CSS |
| **`1fr`** | Unidade do CSS Grid: "1 fração do espaço disponível" |
| **Dark Mode** | Interface com fundo escuro. Reduz fadiga visual em ambientes com pouca luz |
| **Shell Layout** | Padrão de layout com sidebar fixa + área de conteúdo principal scrollável |
| **Design Token** | Nome dado às variáveis de design (cores, tamanhos, espaçamentos) reutilizadas por todo o sistema |
| **Sans-serif** | Família tipográfica SEM serifas (sem "pezinhos" nas letras). Ex: Sora, Helvetica |
| **Serif** | Família tipográfica COM serifas. Ex: DM Serif Display, Times New Roman |
| **Monospace** | Família tipográfica onde todas as letras têm a mesma largura. Ex: DM Mono, Courier |
| **Greedy Algorithm** | Algoritmo guloso — faz sempre a escolha localmente ótima a cada passo, sem olhar para trás |
| **`Intl.NumberFormat`** | API nativa do JavaScript para formatar números com padrões regionais (ex: R$ 1.890,00) |

---

*Projeto: Industrial Optimizer — Teste Técnico Full-Stack (Vue.js 3 + Java Spring Boot)*