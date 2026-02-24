# Industrial Optimizer - Script de Inicialização da Estrutura (PowerShell)
# Execute na raiz do projeto: .\init-structure.ps1

$root = $PSScriptRoot
if (-not $root) { $root = Get-Location }

New-Item -ItemType Directory -Force -Path "$root\backend" | Out-Null
New-Item -ItemType Directory -Force -Path "$root\frontend" | Out-Null
New-Item -ItemType Directory -Force -Path "$root\database" | Out-Null
New-Item -ItemType Directory -Force -Path "$root\docs" | Out-Null

@(
    "$root\database\schema.sql",
    "$root\database\seed.sql",
    "$root\docs\solution-logic.md"
) | ForEach-Object {
    if (-not (Test-Path $_)) { New-Item -ItemType File -Force -Path $_ | Out-Null }
}

Write-Host "Estrutura criada em: $root" -ForegroundColor Green
Get-ChildItem -Path $root -Recurse -Name | Where-Object { $_ -notmatch '^\.git' } | ForEach-Object { Write-Host "  $_" }
