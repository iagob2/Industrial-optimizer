#!/usr/bin/env bash
# Industrial Optimizer - Script de Inicialização da Estrutura (Bash/Git Bash)
# Execute na raiz do projeto: bash init-structure.sh

set -e
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" && pwd)"
cd "$ROOT"

mkdir -p backend frontend database docs

touch database/schema.sql
touch database/seed.sql
touch docs/solution-logic.md

echo "Estrutura criada em: $ROOT"
find . -type f -o -type d | grep -v '^\./\.git' | sort | sed 's|^\./||' | while read -r line; do echo "  $line"; done
