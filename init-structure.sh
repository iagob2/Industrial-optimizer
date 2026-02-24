#!/bin/bash

# Industrial Optimizer - Script de Automação (Bash/Linux/Git Bash)
# Localização: Praia Grande, SP

echo -e "\033[0;36m--- Iniciando Ambiente Industrial Optimizer ---\033[0m"

# Pega o diretório raiz onde o script está
ROOT_DIR=$(pwd)

# 1. Iniciar o Backend (Spring Boot)
echo -e "\033[0;33m[1/2] Subindo Backend na porta 8080...\033[0m"
cd "$ROOT_DIR/backend" || exit

if [ -f "./mvnw" ]; then
    echo "Usando Maven Wrapper..."
    # Abre nova janela no Windows (Git Bash) ou roda em background no Linux
    start bash -c "./mvnw spring-boot:run; exec bash" &
elif command -v mvn &> /dev/null; then
    echo "Usando Maven global..."
    start bash -c "mvn spring-boot:run; exec bash" &
else
    echo -e "\033[0;31mERRO: Maven não encontrado!\033[0m"
fi

# 2. Iniciar o Frontend (Vue.js + Vite)
echo -e "\033[0;33m[2/2] Subindo Frontend na porta 5173...\033[0m"
cd "$ROOT_DIR/frontend" || exit
start bash -c "npm install && npm run dev; exec bash" &

echo -e "\033[0;32m----------------------------------------------\033[0m"
echo -e "\033[0;36mAguardando inicialização (15s) para abrir o navegador...\033[0m"

# Aguarda os 15 segundos que você definiu
sleep 15

# Abre o navegador (comando 'explorer' funciona no Git Bash para Windows)
explorer "http://localhost:5173" || xdg-open "http://localhost:5173" || open "http://localhost:5173"

echo -e "\033[0;32mTudo pronto! Verifique as janelas abertas.\033[0m"