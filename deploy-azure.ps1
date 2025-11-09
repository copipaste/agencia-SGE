# ============================================
# Script de Deployment a Azure App Service
# ============================================

# IMPORTANTE: Ejecutar estos comandos uno por uno y verificar cada paso

# ============================================
# PASO 1: CONFIGURACIÓN INICIAL
# ============================================

# 1.1 Login a Azure
az login

# 1.2 Seleccionar tu suscripción (si tienes varias)
# az account list --output table
# az account set --subscription "TU_SUBSCRIPTION_ID"

# 1.3 Crear grupo de recursos
az group create `
  --name agencia-rg `
  --location eastus

# ============================================
# PASO 2: CREAR AZURE CONTAINER REGISTRY
# ============================================

# 2.1 Crear Container Registry
az acr create `
  --resource-group agencia-rg `
  --name agenciaacr `
  --sku Basic

# 2.2 Login al registry
az acr login --name agenciaacr

# 2.3 Obtener servidor de login
$ACR_LOGIN_SERVER = az acr show --name agenciaacr --query loginServer --output tsv
Write-Host "ACR Login Server: $ACR_LOGIN_SERVER"

# ============================================
# PASO 3: BUILD Y PUSH DE IMÁGENES
# ============================================

# 3.1 Build Backend
Write-Host "Building Backend..."
cd agencia-backend
docker build -t agenciaacr.azurecr.io/agencia-backend:latest .
docker push agenciaacr.azurecr.io/agencia-backend:latest
cd ..

# 3.2 Build Frontend
Write-Host "Building Frontend..."
cd agencia-frontend
docker build -t agenciaacr.azurecr.io/agencia-frontend:latest .
docker push agenciaacr.azurecr.io/agencia-frontend:latest
cd ..

# ============================================
# PASO 4: CREAR APP SERVICE PLAN
# ============================================

# 4.1 Crear plan (B1 = $13 USD/mes)
az appservice plan create `
  --name agencia-plan `
  --resource-group agencia-rg `
  --is-linux `
  --sku B1

# ============================================
# PASO 5: DEPLOY BACKEND
# ============================================

# 5.1 Obtener credenciales del ACR
$ACR_USERNAME = az acr credential show --name agenciaacr --query username --output tsv
$ACR_PASSWORD = az acr credential show --name agenciaacr --query passwords[0].value --output tsv

# 5.2 Crear Web App para Backend
az webapp create `
  --resource-group agencia-rg `
  --plan agencia-plan `
  --name agencia-backend-app `
  --deployment-container-image-name agenciaacr.azurecr.io/agencia-backend:latest

# 5.3 Configurar registry credentials
az webapp config container set `
  --name agencia-backend-app `
  --resource-group agencia-rg `
  --docker-registry-server-url https://agenciaacr.azurecr.io `
  --docker-registry-server-user $ACR_USERNAME `
  --docker-registry-server-password $ACR_PASSWORD

# 5.4 Configurar variables de entorno del Backend
# IMPORTANTE: Cambia estos valores por los tuyos
az webapp config appsettings set `
  --resource-group agencia-rg `
  --name agencia-backend-app `
  --settings `
    MONGODB_URI="mongodb+srv://agencia_user:uagrm2025@agencia-database.8n7ayzu.mongodb.net/?appName=agencia-database" `
    JWT_SECRET="tu_clave_secreta_super_segura_cambiala_en_produccion_12345" `
    SERVER_PORT="8080" `
    CORS_ALLOWED_ORIGINS="https://agencia-frontend-app.azurewebsites.net"

# 5.5 Habilitar logs del contenedor
az webapp log config `
  --name agencia-backend-app `
  --resource-group agencia-rg `
  --docker-container-logging filesystem

# 5.6 Restart backend
az webapp restart --name agencia-backend-app --resource-group agencia-rg

Write-Host "Backend URL: https://agencia-backend-app.azurewebsites.net"

# ============================================
# PASO 6: DEPLOY FRONTEND
# ============================================

# 6.1 Crear Web App para Frontend
az webapp create `
  --resource-group agencia-rg `
  --plan agencia-plan `
  --name agencia-frontend-app `
  --deployment-container-image-name agenciaacr.azurecr.io/agencia-frontend:latest

# 6.2 Configurar registry credentials
az webapp config container set `
  --name agencia-frontend-app `
  --resource-group agencia-rg `
  --docker-registry-server-url https://agenciaacr.azurecr.io `
  --docker-registry-server-user $ACR_USERNAME `
  --docker-registry-server-password $ACR_PASSWORD

# 6.3 Configurar puerto (NGINX usa puerto 80)
az webapp config appsettings set `
  --resource-group agencia-rg `
  --name agencia-frontend-app `
  --settings `
    WEBSITES_PORT="80" `
    BACKEND_URL="https://agencia-backend-app.azurewebsites.net"

# 6.4 Habilitar logs
az webapp log config `
  --name agencia-frontend-app `
  --resource-group agencia-rg `
  --docker-container-logging filesystem

# 6.5 Restart frontend
az webapp restart --name agencia-frontend-app --resource-group agencia-rg

Write-Host "Frontend URL: https://agencia-frontend-app.azurewebsites.net"

# ============================================
# PASO 7: VERIFICAR DEPLOYMENT
# ============================================

Write-Host "`n============================================"
Write-Host "DEPLOYMENT COMPLETADO"
Write-Host "============================================"
Write-Host "Backend: https://agencia-backend-app.azurewebsites.net"
Write-Host "Frontend: https://agencia-frontend-app.azurewebsites.net"
Write-Host "GraphQL: https://agencia-backend-app.azurewebsites.net/graphql"
Write-Host "GraphiQL: https://agencia-backend-app.azurewebsites.net/graphiql"
Write-Host "`nPara ver logs:"
Write-Host "Backend: az webapp log tail --name agencia-backend-app --resource-group agencia-rg"
Write-Host "Frontend: az webapp log tail --name agencia-frontend-app --resource-group agencia-rg"
Write-Host "============================================"

# ============================================
# COMANDOS ÚTILES
# ============================================

# Ver logs en tiempo real del backend
# az webapp log tail --name agencia-backend-app --resource-group agencia-rg

# Ver logs en tiempo real del frontend
# az webapp log tail --name agencia-frontend-app --resource-group agencia-rg

# Reiniciar backend
# az webapp restart --name agencia-backend-app --resource-group agencia-rg

# Reiniciar frontend
# az webapp restart --name agencia-frontend-app --resource-group agencia-rg

# Ver estado de las apps
# az webapp show --name agencia-backend-app --resource-group agencia-rg --query state
# az webapp show --name agencia-frontend-app --resource-group agencia-rg --query state

# Actualizar imagen del backend
# docker build -t agenciaacr.azurecr.io/agencia-backend:latest ./agencia-backend
# docker push agenciaacr.azurecr.io/agencia-backend:latest
# az webapp restart --name agencia-backend-app --resource-group agencia-rg

# Actualizar imagen del frontend
# docker build -t agenciaacr.azurecr.io/agencia-frontend:latest ./agencia-frontend
# docker push agenciaacr.azurecr.io/agencia-frontend:latest
# az webapp restart --name agencia-frontend-app --resource-group agencia-rg

# Eliminar todo (para empezar de nuevo)
# az group delete --name agencia-rg --yes --no-wait
