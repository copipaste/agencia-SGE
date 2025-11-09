# deploy.ps1
# Script para desplegar cambios en Azure
# Uso: .\deploy.ps1 -Target [backend|frontend|both]

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("backend", "frontend", "both")]
    [string]$Target = "both"
)

# Colores
$ColorCyan = "Cyan"
$ColorYellow = "Yellow"
$ColorGreen = "Green"
$ColorRed = "Red"

Write-Host "🚀 Iniciando despliegue de Agencia de Viajes..." -ForegroundColor $ColorCyan
Write-Host ""

# Verificar que Docker está corriendo
try {
    docker ps | Out-Null
} catch {
    Write-Host "❌ ERROR: Docker no está ejecutándose. Inicia Docker Desktop primero." -ForegroundColor $ColorRed
    exit 1
}

# Verificar login en Azure
try {
    az account show | Out-Null
} catch {
    Write-Host "❌ ERROR: No estás logueado en Azure CLI." -ForegroundColor $ColorRed
    Write-Host "Ejecuta: az login" -ForegroundColor $ColorYellow
    exit 1
}

# Variables
$ResourceGroup = "agencia"
$RegistryName = "agencia"
$BackendApp = "agencia-backend-app"
$FrontendApp = "agencia-frontend-app"
$RegistryUrl = "agencia.azurecr.io"

# Función para desplegar backend
function Deploy-Backend {
    Write-Host "📦 ACTUALIZANDO BACKEND..." -ForegroundColor $ColorYellow
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $ColorYellow
    
    # Navegar a carpeta backend
    Push-Location agencia-backend
    
    Write-Host "🔨 Construyendo imagen Docker del backend..." -ForegroundColor $ColorCyan
    docker build -t "$RegistryUrl/agencia-backend:latest" .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al construir imagen del backend" -ForegroundColor $ColorRed
        Pop-Location
        return $false
    }
    
    Write-Host "📤 Subiendo imagen al Container Registry..." -ForegroundColor $ColorCyan
    docker push "$RegistryUrl/agencia-backend:latest"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al subir imagen del backend" -ForegroundColor $ColorRed
        Pop-Location
        return $false
    }
    
    Write-Host "🔄 Reiniciando Web App del backend..." -ForegroundColor $ColorCyan
    az webapp restart --name $BackendApp --resource-group $ResourceGroup
    
    Pop-Location
    
    Write-Host "✅ Backend actualizado correctamente" -ForegroundColor $ColorGreen
    Write-Host ""
    return $true
}

# Función para desplegar frontend
function Deploy-Frontend {
    Write-Host "🎨 ACTUALIZANDO FRONTEND..." -ForegroundColor $ColorYellow
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $ColorYellow
    
    # Navegar a carpeta frontend
    Push-Location agencia-frontend
    
    Write-Host "🔨 Construyendo imagen Docker del frontend..." -ForegroundColor $ColorCyan
    docker build -t "$RegistryUrl/agencia-frontend:latest" .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al construir imagen del frontend" -ForegroundColor $ColorRed
        Pop-Location
        return $false
    }
    
    Write-Host "📤 Subiendo imagen al Container Registry..." -ForegroundColor $ColorCyan
    docker push "$RegistryUrl/agencia-frontend:latest"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error al subir imagen del frontend" -ForegroundColor $ColorRed
        Pop-Location
        return $false
    }
    
    Write-Host "🔧 Actualizando configuración del contenedor..." -ForegroundColor $ColorCyan
    az webapp config container set --name $FrontendApp --resource-group $ResourceGroup --docker-custom-image-name "$RegistryUrl/agencia-frontend:latest" | Out-Null
    
    Write-Host "🔄 Reiniciando Web App del frontend..." -ForegroundColor $ColorCyan
    az webapp restart --name $FrontendApp --resource-group $ResourceGroup
    
    Pop-Location
    
    Write-Host "✅ Frontend actualizado correctamente" -ForegroundColor $ColorGreen
    Write-Host ""
    return $true
}

# Ejecutar despliegue según el target
$success = $true

if ($Target -eq "backend" -or $Target -eq "both") {
    $success = Deploy-Backend
}

if ($success -and ($Target -eq "frontend" -or $Target -eq "both")) {
    $success = Deploy-Frontend
}

# Resumen final
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $ColorCyan
if ($success) {
    Write-Host "🎉 DESPLIEGUE COMPLETADO EXITOSAMENTE" -ForegroundColor $ColorGreen
    Write-Host ""
    Write-Host "⏱️  Espera 1-2 minutos para que los cambios se apliquen." -ForegroundColor $ColorYellow
    Write-Host ""
    Write-Host "🔗 URLs de la aplicación:" -ForegroundColor $ColorCyan
    Write-Host "   🌐 Frontend:  https://agencia-frontend-app.azurewebsites.net" -ForegroundColor White
    Write-Host "   ⚙️  Backend:   https://agencia-backend-app.azurewebsites.net" -ForegroundColor White
    Write-Host "   🔌 GraphQL:   https://agencia-backend-app.azurewebsites.net/graphql" -ForegroundColor White
    Write-Host "   📊 GraphiQL:  https://agencia-backend-app.azurewebsites.net/graphiql" -ForegroundColor White
} else {
    Write-Host "❌ DESPLIEGUE FALLIDO" -ForegroundColor $ColorRed
    Write-Host "Revisa los errores anteriores para más detalles." -ForegroundColor $ColorYellow
    exit 1
}
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $ColorCyan
Write-Host ""
