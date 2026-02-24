# Industrial Optimizer - Script de Automação Full Stack
# Localização: Praia Grande, SP

# Execute na raiz do projeto: .\init-structure.ps1



$root = $PSScriptRoot
if (-not $root) { $root = Get-Location }

Write-Host "--- Iniciando Ambiente Industrial Optimizer ---" -ForegroundColor Cyan

# 1. Iniciar o Backend (Spring Boot)
Write-Host "[1/2] Subindo Backend na porta 8080..." -ForegroundColor Yellow
$backendPath = Join-Path $root "backend"
$backendCommand = @"
cd '$backendPath'
if (Test-Path .\mvnw.cmd) {
    Write-Host 'Usando Maven Wrapper...' -ForegroundColor Cyan
    .\mvnw.cmd spring-boot:run
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host 'Usando Maven global...' -ForegroundColor Cyan
    mvn spring-boot:run
} else {
    Write-Host 'ERRO: Maven não encontrado! Instale Maven ou execute: mvn wrapper:wrapper' -ForegroundColor Red
    Write-Host 'Download: https://maven.apache.org/download.cgi' -ForegroundColor Yellow
    Read-Host 'Pressione ENTER para sair'
}
"@
Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendCommand -WindowStyle Normal

# 2. Iniciar o Frontend (Vue.js + Vite)
Write-Host "[2/2] Subindo Frontend na porta 5173..." -ForegroundColor Yellow
$frontendPath = Join-Path $root "frontend"
$frontendCommand = "cd '$frontendPath'; npm install; npm run dev"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $frontendCommand -WindowStyle Normal

Write-Host "----------------------------------------------" -ForegroundColor Green
Write-Host "Aguardando inicialização para abrir o navegador..." -ForegroundColor Cyan

# Aguarda 15 segundos para o Vite subir o servidor antes de abrir
Start-Sleep -Seconds 15

# Abre o navegador padrão na URL do frontend
Start-Process "http://localhost:5173"

Write-Host "Tudo pronto! Verifique as janelas abertas." -ForegroundColor Green
Write-Host "Backend: http://localhost:8080" -ForegroundColor White
Write-Host "Frontend: http://localhost:5173" -ForegroundColor White