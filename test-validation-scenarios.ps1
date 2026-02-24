# Script para testar os 4 cenários de validação
# Industrial Optimizer - Data Integrity Validation Tests

$baseUrl = "http://localhost:8080/api/v1"
$header = @{
    "Content-Type" = "application/json"
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "INDUSTRIAL OPTIMIZER - VALIDATION TESTS"
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Função auxiliar para fazer requisições e exibir respostas
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body,
        [string]$Description
    )
    
    Write-Host "$Description" -ForegroundColor Yellow
    Write-Host "Método: $Method"
    Write-Host "URL: $Url"
    if ($Body) {
        Write-Host "Body: $Body"
    }
    Write-Host ""
    
    try {
        if ($Method -eq "GET" -or $Method -eq "DELETE") {
            $response = Invoke-WebRequest -Uri $Url -Method $Method -Headers $header -ErrorAction SilentlyContinue
        } else {
            $response = Invoke-WebRequest -Uri $Url -Method $Method -Headers $header -Body $Body -ErrorAction SilentlyContinue
        }
        
        Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Response:" 
        Write-Host ($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 3)
    } catch {
        $response = $_.Exception.Response
        Write-Host "✗ Status: $($response.StatusCode)" -ForegroundColor Red
        Write-Host "Response:"
        Write-Host ($_.Exception.Message)
        if ($_.Exception.Response) {
            try {
                $errorBody = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream()).ReadToEnd()
                Write-Host $errorBody
            } catch {}
        }
    }
    
    Write-Host ""
    Write-Host "---" -ForegroundColor Gray
    Write-Host ""
}

# CENÁRIO 1: NEGATIVE VALUE (Negative Price)
Write-Host "CENÁRIO 1: NEGATIVE VALUE - Validação de Valor Negativo" -ForegroundColor Magenta
Write-Host "_" * 80
Write-Host ""

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/products" `
    -Body '{"code":"PROD-NEG-001","name":"Product Negative","saleValue":-100.00}' `
    -Description "❌ Teste 1.1: Criar produto com preço NEGATIVO -100.00"

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/products" `
    -Body '{"code":"PROD-ZERO-001","name":"Product Zero","saleValue":0.00}' `
    -Description "❌ Teste 1.2: Criar produto com preço ZERO"

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/products" `
    -Body '{"code":"PROD-VALID-001","name":"Product Valid","saleValue":150.50}' `
    -Description "✓ Teste 1.3: Criar produto com preço VÁLIDO 150.50"

Write-Host ""
Write-Host ""

# CENÁRIO 2: NEGATIVE STOCK
Write-Host "CENÁRIO 2: NEGATIVE STOCK - Validação de Estoque Negativo" -ForegroundColor Magenta
Write-Host "_" * 80
Write-Host ""

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/raw-materials" `
    -Body '{"code":"MAT-NEG-001","name":"Material Negative","unitMeasure":"KG","stockQuantity":-50.00,"unitCost":10.00}' `
    -Description "❌ Teste 2.1: Criar matéria-prima com estoque NEGATIVO -50"

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/raw-materials" `
    -Body '{"code":"MAT-VALID-001","name":"Material Valid","unitMeasure":"KG","stockQuantity":100.00,"unitCost":10.00}' `
    -Description "✓ Teste 2.2: Criar matéria-prima com estoque VÁLIDO 100"

Write-Host ""
Write-Host ""

# CENÁRIO 3: DUPLICATED CODES
Write-Host "CENÁRIO 3: DUPLICATED CODES - Validação de Códigos Duplicados" -ForegroundColor Magenta
Write-Host "_" * 80
Write-Host ""

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/products" `
    -Body '{"code":"PROD-DUP-001","name":"Product Duplicate 1","saleValue":200.00}' `
    -Description "✓ Teste 3.1: Criar produto com código PROD-DUP-001 (primeira vez)"

Test-Endpoint -Method "POST" `
    -Url "$baseUrl/products" `
    -Body '{"code":"PROD-DUP-001","name":"Product Duplicate 2","saleValue":300.00}' `
    -Description "❌ Teste 3.2: Tentar criar produto com código DUPLICADO PROD-DUP-001"

Write-Host ""
Write-Host ""

# CENÁRIO 4: CASCADE DELETION
Write-Host "CENÁRIO 4: CASCADE DELETION - Validação de Exclusão em Cascata" -ForegroundColor Magenta
Write-Host "_" * 80
Write-Host ""

# Primeiro, criar uma matéria-prima que será usada em uma composição
$matResponse = Test-Endpoint -Method "POST" `
    -Url "$baseUrl/raw-materials" `
    -Body '{"code":"MAT-CASCADE-001","name":"Material for Cascade","unitMeasure":"KG","stockQuantity":100.00,"unitCost":25.00}' `
    -Description "✓ Teste 4.1: Criar matéria-prima MAT-CASCADE-001 para teste de cascata"

Write-Host ""
Write-Host "CONTINUAÇÃO DO CENÁRIO 4:" -ForegroundColor Magenta
Write-Host "Nota: Para testar completamente o cenário 4, você precisa:"
Write-Host "  1. Obter o ID da matéria-prima criada acima"
Write-Host "  2. Criar um produto que use essa matéria-prima em sua composição"
Write-Host "  3. Tentar deletar a matéria-prima (deve falhar com erro 409 Conflict)"
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTES CONCLUÍDOS"
Write-Host "========================================" -ForegroundColor Cyan
