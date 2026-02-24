# Industrial Production Optimizer

🌎 English | [🇧🇷 Português](./READMEo_pt.md)

Production planning playground for a furniture factory.  
This project demonstrates how to maximize profit given limited raw material stock using a greedy optimization algorithm, implemented with **Spring Boot** and **Vue 3**.



---

## 📖 Overview

Industrial Optimizer is a full-stack technical challenge that simulates a furniture factory:

* **Backend (Spring Boot)**
  * Exposes REST endpoints for Products, Raw Materials, and Product Compositions.
  * Implements a **greedy optimization service** that suggests how many units of each product should be produced to maximize profit, given the current stock of raw materials.
* **Frontend (Vue 3 + Vite)**
  * A single-page dashboard with:
    * Optimization CTA + total profit hero.
    * Production plan cards (what to produce and why).
    * Stock consumption visualization and shortage alerts.
    * CRUD screens for Products, Raw Materials, and Recipes (compositions).

The optimization logic is designed to be easy to reason about during a code review, while still being realistic enough to exercise domain modeling, SQL schema design, and API/front-end integration.

Deep-dive documentation lives in [`/docs`](./docs).

---

## 🖼️ Screenshots

| Optimization | Products |
|---|---|
| ![Optimization Screen](./docs/imagens/Industrial_Optimizer.pdf) | ![Products Screen](./docs/imagens/produtos.pdf) |

| Raw Materials | Composition |
|---|---|
| ![Raw Materials Screen](./docs/imagens/insumos.pdf) | ![Composition Screen](./docs/imagens/composição_dos_produtos.pdf) |

---

## ⚙️ Prerequisites

Make sure your machine has:
* **Java 17 or 21** (Any recent distribution is fine, Temurin / OpenJDK recommended)
* **Node.js 18+** and **npm**
* **Git** (to clone the repository)

---

## 📂 Project Structure

```text
industrial-optimizer/
├── backend/                        # Spring Boot application (REST API + optimizer)
├── frontend/                       # Vue 3 + Vite SPA (dashboard and CRUD)
├── database/                       # schema.sql and seed.sql
├── docs/                           # architecture, algorithm, testing and validation docs
├── init-structure.sh               # Unix/macOS helper script to start everything
├── init-structure.ps1              # Windows helper script to start everything
├── test-validation-scenarios.ps1   # Windows script for automated API validation testing
└── README.md                       # This file
```

---

## 🚀 How to Run

You can use the automation scripts (recommended for reviewers) or the manual commands.

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/industrial-optimizer.git
cd industrial-optimizer
```

### 2. One-shot startup (Scripts)

The provided scripts will automatically install dependencies, start the Spring Boot backend on port 8080, and launch the Vue 3 frontend dev server on port 5173.

**On Windows (PowerShell):**

```powershell
.\init-structure.ps1
```

**On Unix/macOS:**

```bash
chmod +x init-structure.sh
./init-structure.sh
```

### 3. Manual Run (If you prefer full control)

**Backend (Spring Boot):**

```bash
cd backend
./mvnw spring-boot:run  # On Windows use: .\mvnw.cmd spring-boot:run
```

**Frontend (Vue 3 + Vite):**

```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 How to Test

This section covers the commands to run the tests and the step-by-step workflow describing how the tests validate the system.

### 1. Commands

**Backend Unit & Integration Tests (Spring Boot):**

```bash
cd backend
./mvnw test  # On Windows use: .\mvnw.cmd test
```

**Backend Validation Scenarios (Automated Script):**  
Make sure the backend is running before executing this script.

```powershell
.\test-validation-scenarios.ps1
```

**Frontend Tests (Vitest + Vue Test Utils):**

```bash
cd frontend
npm install
npm run test:unit
```

### 2. Testing Workflow (Step-by-Step)

#### 2.1 Backend Testing Flow

* **Domain & Schema Validation:** Tests ensure the N:M relationship (Product × Raw Material via ProductComposition) matches the intended business model and SQL schema.
* **Optimization Service Tests:** Unit tests target the `ProductionOptimizerService`. Given a seeded stock and product catalog, it validates if the service sorts products by profitability, respects raw material constraints, and handles edge cases (stock exhaustion, zero-profit products).
* **API Contract Sanity:** Tests for controllers verify that `/api/products`, `/api/raw-materials`, and `/api/product-compositions` return the correct HTTP status codes (200/201/204) and DTO serialization.

#### 2.2 Frontend Testing Flow

* **Loading State & UX:** Verifies that clicking the optimization CTA disables the button and shows a loading state, preventing double clicks.
* **Formatting:** Ensures the total projected profit is rendered correctly in BRL currency.
* **Error Feedback:** Mocks a 500 error from the API to validate that the component shows a clear, user-friendly error message in the UI.
* **API Mocking:** Uses `vi.mock` to simulate endpoints (`getMaterials`, `getProducts`, `getOptimization`) to focus strictly on UI behavior and reactivity without needing a live backend.

---

## 📚 Documentation Map

If you want to dive deeper into specific parts of the solution:

| Document | Path |
|---|---|
| Backend Architecture & Engineering | `docs/backend/backend_engineering.md` |
| Frontend Architecture & UX | `docs/frontend/Documentacao_frontend.md` |
| Algorithm & Solution Logic | `docs/architecture/solution-logic.md` |
| Data Integrity & Validation Scenarios | `docs/validation/VALIDATION_AND_INTEGRITY_GUIDE.md` |
| Full File Index | `docs/meta/FILE_INDEX.md` |