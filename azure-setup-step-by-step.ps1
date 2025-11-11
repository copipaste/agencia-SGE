# ============================================
# GUÍA PASO A PASO - DEPLOYMENT A AZURE
# ============================================
# Ejecuta cada sección UNA POR UNA y verifica que funcione

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "DEPLOYMENT A AZURE - PASO A PASO" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "IMPORTANTE: Ejecuta cada paso manualmente copiando y pegando los comandos" -ForegroundColor Yellow
Write-Host ""

# ============================================
# PASO 1: LOGIN A AZURE
# ============================================
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 1: LOGIN A AZURE" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ejecuta este comando:" -ForegroundColor Yellow
Write-Host "az login" -ForegroundColor White
Write-Host ""
Write-Host "Se abrirá tu navegador. Inicia sesión con tu cuenta de Azure/Microsoft" -ForegroundColor Cyan
Write-Host "Si no tienes cuenta, créala gratis en: https://azure.microsoft.com/free" -ForegroundColor Cyan
Write-Host ""
Write-Host "Presiona ENTER cuando hayas completado el login..." -ForegroundColor Yellow
pause

# ============================================
# PASO 2: CREAR GRUPO DE RECURSOS
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 2: CREAR GRUPO DE RECURSOS" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Un grupo de recursos es un contenedor para todos los recursos de Azure" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ejecuta este comando:" -ForegroundColor Yellow
Write-Host "az group create --name agencia-rg --location eastus" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado el comando..." -ForegroundColor Yellow
pause

# ============================================
# PASO 3: CREAR AZURE CONTAINER REGISTRY
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 3: CREAR CONTAINER REGISTRY" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Aquí guardaremos las imágenes Docker (como Docker Hub privado)" -ForegroundColor Cyan
Write-Host "Costo: ~$5 USD/mes" -ForegroundColor Yellow
Write-Host ""
Write-Host "Ejecuta estos comandos UNO POR UNO:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Crear el registry:" -ForegroundColor White
Write-Host "   az acr create --resource-group agencia-rg --name agenciaacr --sku Basic" -ForegroundColor White
Write-Host ""
Write-Host "2. Login al registry:" -ForegroundColor White
Write-Host "   az acr login --name agenciaacr" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado AMBOS comandos..." -ForegroundColor Yellow
pause

# ============================================
# PASO 4: BUILD BACKEND (DOCKER)
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 4: BUILD IMAGEN DOCKER DEL BACKEND" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Esto puede tardar 5-10 minutos la primera vez..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Ejecuta estos comandos:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Ir a la carpeta del backend:" -ForegroundColor White
Write-Host "   cd agencia-backend" -ForegroundColor White
Write-Host ""
Write-Host "2. Build de la imagen:" -ForegroundColor White
Write-Host "   docker build -t agenciaacr.azurecr.io/agencia-backend:latest ." -ForegroundColor White
Write-Host ""
Write-Host "3. Push al registry:" -ForegroundColor White
Write-Host "   docker push agenciaacr.azurecr.io/agencia-backend:latest" -ForegroundColor White
Write-Host ""
Write-Host "4. Volver a la raíz:" -ForegroundColor White
Write-Host "   cd .." -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas completado el build y push..." -ForegroundColor Yellow
pause

# ============================================
# PASO 5: BUILD FRONTEND (DOCKER)
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 5: BUILD IMAGEN DOCKER DEL FRONTEND" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Esto también puede tardar unos minutos..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Ejecuta estos comandos:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Ir a la carpeta del frontend:" -ForegroundColor White
Write-Host "   cd agencia-frontend" -ForegroundColor White
Write-Host ""
Write-Host "2. Build de la imagen:" -ForegroundColor White
Write-Host "   docker build -t agenciaacr.azurecr.io/agencia-frontend:latest ." -ForegroundColor White
Write-Host ""
Write-Host "3. Push al registry:" -ForegroundColor White
Write-Host "   docker push agenciaacr.azurecr.io/agencia-frontend:latest" -ForegroundColor White
Write-Host ""
Write-Host "4. Volver a la raíz:" -ForegroundColor White
Write-Host "   cd .." -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas completado el build y push..." -ForegroundColor Yellow
pause

# ============================================
# PASO 6: CREAR APP SERVICE PLAN
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 6: CREAR APP SERVICE PLAN" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "El plan define los recursos (CPU, RAM) que usarán tus apps" -ForegroundColor Cyan
Write-Host "Plan B1: 1 CPU, 1.75GB RAM - Costo: ~$13 USD/mes" -ForegroundColor Yellow
Write-Host ""
Write-Host "Ejecuta este comando:" -ForegroundColor Yellow
Write-Host "az appservice plan create --name agencia-plan --resource-group agencia-rg --is-linux --sku B1" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado el comando..." -ForegroundColor Yellow
pause

# ============================================
# PASO 7: OBTENER CREDENCIALES DEL REGISTRY
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 7: OBTENER CREDENCIALES" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ejecuta estos comandos y GUARDA los valores:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Username:" -ForegroundColor White
Write-Host '   $ACR_USERNAME = az acr credential show --name agenciaacr --query username --output tsv' -ForegroundColor White
Write-Host '   echo "Username: $ACR_USERNAME"' -ForegroundColor White
Write-Host ""
Write-Host "2. Password:" -ForegroundColor White
Write-Host '   $ACR_PASSWORD = az acr credential show --name agenciaacr --query passwords[0].value --output tsv' -ForegroundColor White
Write-Host '   echo "Password: $ACR_PASSWORD"' -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado los comandos..." -ForegroundColor Yellow
pause

# ============================================
# PASO 8: DEPLOY BACKEND
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 8: DEPLOY BACKEND A AZURE" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ejecuta estos comandos UNO POR UNO:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Crear Web App:" -ForegroundColor White
Write-Host "   az webapp create --resource-group agencia-rg --plan agencia-plan --name agencia-backend-app --deployment-container-image-name agenciaacr.azurecr.io/agencia-backend:latest" -ForegroundColor White
Write-Host ""
Write-Host "2. Configurar registry:" -ForegroundColor White
Write-Host '   az webapp config container set --name agencia-backend-app --resource-group agencia-rg --docker-registry-server-url https://agenciaacr.azurecr.io --docker-registry-server-user $ACR_USERNAME --docker-registry-server-password $ACR_PASSWORD' -ForegroundColor White
Write-Host ""
Write-Host "3. Configurar variables de entorno (IMPORTANTE - USA TUS DATOS):" -ForegroundColor White
Write-Host '   az webapp config appsettings set --resource-group agencia-rg --name agencia-backend-app --settings MONGODB_URI="mongodb+srv://agencia_user:uagrm2025@agencia-database.8n7ayzu.mongodb.net/?appName=agencia-database" JWT_SECRET="tu_clave_secreta_super_segura_12345" SERVER_PORT="8080" CORS_ALLOWED_ORIGINS="https://agencia-frontend-app.azurewebsites.net"' -ForegroundColor White
Write-Host ""
Write-Host "4. Habilitar logs:" -ForegroundColor White
Write-Host "   az webapp log config --name agencia-backend-app --resource-group agencia-rg --docker-container-logging filesystem" -ForegroundColor White
Write-Host ""
Write-Host "5. Reiniciar:" -ForegroundColor White
Write-Host "   az webapp restart --name agencia-backend-app --resource-group agencia-rg" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado TODOS los comandos..." -ForegroundColor Yellow
pause

# ============================================
# PASO 9: DEPLOY FRONTEND
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 9: DEPLOY FRONTEND A AZURE" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ejecuta estos comandos UNO POR UNO:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Crear Web App:" -ForegroundColor White
Write-Host "   az webapp create --resource-group agencia-rg --plan agencia-plan --name agencia-frontend-app --deployment-container-image-name agenciaacr.azurecr.io/agencia-frontend:latest" -ForegroundColor White
Write-Host ""
Write-Host "2. Configurar registry:" -ForegroundColor White
Write-Host '   az webapp config container set --name agencia-frontend-app --resource-group agencia-rg --docker-registry-server-url https://agenciaacr.azurecr.io --docker-registry-server-user $ACR_USERNAME --docker-registry-server-password $ACR_PASSWORD' -ForegroundColor White
Write-Host ""
Write-Host "3. Configurar puerto:" -ForegroundColor White
Write-Host '   az webapp config appsettings set --resource-group agencia-rg --name agencia-frontend-app --settings WEBSITES_PORT="80" BACKEND_URL="https://agencia-backend-app.azurewebsites.net"' -ForegroundColor White
Write-Host ""
Write-Host "4. Habilitar logs:" -ForegroundColor White
Write-Host "   az webapp log config --name agencia-frontend-app --resource-group agencia-rg --docker-container-logging filesystem" -ForegroundColor White
Write-Host ""
Write-Host "5. Reiniciar:" -ForegroundColor White
Write-Host "   az webapp restart --name agencia-frontend-app --resource-group agencia-rg" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER cuando hayas ejecutado TODOS los comandos..." -ForegroundColor Yellow
pause

# ============================================
# PASO 10: VERIFICAR DEPLOYMENT
# ============================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "PASO 10: VERIFICAR DEPLOYMENT" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "¡LISTO! Tu aplicación está desplegada 🎉" -ForegroundColor Green
Write-Host ""
Write-Host "URLs de tu aplicación:" -ForegroundColor Cyan
Write-Host "  Frontend: https://agencia-frontend-app.azurewebsites.net" -ForegroundColor White
Write-Host "  Backend:  https://agencia-backend-app.azurewebsites.net" -ForegroundColor White
Write-Host "  GraphQL:  https://agencia-backend-app.azurewebsites.net/graphql" -ForegroundColor White
Write-Host "  GraphiQL: https://agencia-backend-app.azurewebsites.net/graphiql" -ForegroundColor White
Write-Host ""
Write-Host "Comandos útiles:" -ForegroundColor Yellow
Write-Host ""
Write-Host "Ver logs del backend:" -ForegroundColor White
Write-Host "  az webapp log tail --name agencia-backend-app --resource-group agencia-rg" -ForegroundColor Gray
Write-Host ""
Write-Host "Ver logs del frontend:" -ForegroundColor White
Write-Host "  az webapp log tail --name agencia-frontend-app --resource-group agencia-rg" -ForegroundColor Gray
Write-Host ""
Write-Host "Abrir frontend en navegador:" -ForegroundColor White
Write-Host "  start https://agencia-frontend-app.azurewebsites.net" -ForegroundColor Gray
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "DEPLOYMENT COMPLETADO" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
