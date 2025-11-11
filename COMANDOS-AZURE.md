# GUÍA RÁPIDA - COMANDOS PARA COPIAR Y PEGAR

## PASO 1: LOGIN
```powershell
az login
```

## PASO 2: CREAR GRUPO DE RECURSOS
```powershell
az group create --name agencia-rg --location eastus
```

## PASO 3: CREAR CONTAINER REGISTRY
```powershell
az acr create --resource-group agencia-rg --name agenciaacr --sku Basic
az acr login --name agenciaacr
```

## PASO 4: BUILD Y PUSH BACKEND
```powershell
cd agencia-backend
docker build -t agenciaacr.azurecr.io/agencia-backend:latest .
docker push agenciaacr.azurecr.io/agencia-backend:latest
cd ..
```

## PASO 5: BUILD Y PUSH FRONTEND
```powershell
cd agencia-frontend
docker build -t agenciaacr.azurecr.io/agencia-frontend:latest .
docker push agenciaacr.azurecr.io/agencia-frontend:latest
cd ..
```

## PASO 6: CREAR APP SERVICE PLAN
```powershell
az appservice plan create --name agencia-plan --resource-group agencia-rg --is-linux --sku B1
```

## PASO 7: OBTENER CREDENCIALES
```powershell
$ACR_USERNAME = az acr credential show --name agenciaacr --query username --output tsv
$ACR_PASSWORD = az acr credential show --name agenciaacr --query passwords[0].value --output tsv
echo "Username: $ACR_USERNAME"
echo "Password: $ACR_PASSWORD"
```

## PASO 8: DEPLOY BACKEND
```powershell
# 1. Crear Web App
az webapp create --resource-group agencia-rg --plan agencia-plan --name agencia-backend-app --deployment-container-image-name agenciaacr.azurecr.io/agencia-backend:latest

# 2. Configurar registry
az webapp config container set --name agencia-backend-app --resource-group agencia-rg --docker-registry-server-url https://agenciaacr.azurecr.io --docker-registry-server-user $ACR_USERNAME --docker-registry-server-password $ACR_PASSWORD

# 3. Variables de entorno
az webapp config appsettings set --resource-group agencia-rg --name agencia-backend-app --settings MONGODB_URI="mongodb+srv://agencia_user:uagrm2025@agencia-database.8n7ayzu.mongodb.net/?appName=agencia-database" JWT_SECRET="tu_clave_secreta_super_segura_12345" SERVER_PORT="8080" CORS_ALLOWED_ORIGINS="https://agencia-frontend-app.azurewebsites.net"

# 4. Habilitar logs
az webapp log config --name agencia-backend-app --resource-group agencia-rg --docker-container-logging filesystem

# 5. Reiniciar
az webapp restart --name agencia-backend-app --resource-group agencia-rg
```

## PASO 9: DEPLOY FRONTEND
```powershell
# 1. Crear Web App
az webapp create --resource-group agencia-rg --plan agencia-plan --name agencia-frontend-app --deployment-container-image-name agenciaacr.azurecr.io/agencia-frontend:latest

# 2. Configurar registry
az webapp config container set --name agencia-frontend-app --resource-group agencia-rg --docker-registry-server-url https://agenciaacr.azurecr.io --docker-registry-server-user $ACR_USERNAME --docker-registry-server-password $ACR_PASSWORD

# 3. Configurar puerto
az webapp config appsettings set --resource-group agencia-rg --name agencia-frontend-app --settings WEBSITES_PORT="80" BACKEND_URL="https://agencia-backend-app.azurewebsites.net"

# 4. Habilitar logs
az webapp log config --name agencia-frontend-app --resource-group agencia-rg --docker-container-logging filesystem

# 5. Reiniciar
az webapp restart --name agencia-frontend-app --resource-group agencia-rg
```

## URLs FINALES
- Frontend: https://agencia-frontend-app.azurewebsites.net
- Backend: https://agencia-backend-app.azurewebsites.net
- GraphQL: https://agencia-backend-app.azurewebsites.net/graphql

## COMANDOS ÚTILES
```powershell
# Ver logs backend
az webapp log tail --name agencia-backend-app --resource-group agencia-rg

# Ver logs frontend
az webapp log tail --name agencia-frontend-app --resource-group agencia-rg

# Reiniciar backend
az webapp restart --name agencia-backend-app --resource-group agencia-rg

# Abrir en navegador
start https://agencia-frontend-app.azurewebsites.net
```
