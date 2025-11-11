# 🚀 Guía de Deployment a Azure con Docker

Esta guía te llevará paso a paso para desplegar tu aplicación en Azure App Service usando Docker.

## 📋 Pre-requisitos

1. **Azure CLI instalado**
   ```bash
   winget install Microsoft.AzureCLI
   ```

2. **Docker Desktop instalado y ejecutándose**
   - Descargar de: https://www.docker.com/products/docker-desktop

3. **Cuenta de Azure**
   - Crear cuenta gratuita: https://azure.microsoft.com/free
   - Obtienes $200 USD en créditos por 30 días

## 🏗️ Arquitectura

```
Internet
   ↓
Azure Frontend App (NGINX + Angular)
   ↓ Proxy /api y /graphql
Azure Backend App (Spring Boot)
   ↓
MongoDB Atlas (ya desplegado)
```

## 💰 Costos Estimados

- **App Service Plan B1**: ~$13 USD/mes
- **Azure Container Registry**: ~$5 USD/mes
- **Total**: ~$18 USD/mes

## 🚀 Pasos de Deployment

### Opción A: Deployment Manual (PowerShell)

1. **Abrir PowerShell como Administrador**

2. **Navegar al directorio del proyecto**
   ```powershell
   cd "f:\JHOEL\SEMESTRE 2-2025\SW2\EXAMEN1"
   ```

3. **Ejecutar el script de deployment**
   ```powershell
   .\deploy-azure.ps1
   ```

4. **Seguir las instrucciones en pantalla**
   - Login a Azure cuando se solicite
   - Esperar a que se completen todos los pasos

### Opción B: Deployment Automático (GitHub Actions)

1. **Configurar secreto de Azure en GitHub**

   a. Crear Service Principal:
   ```bash
   az ad sp create-for-rbac --name "agencia-github-sp" --role contributor \
     --scopes /subscriptions/{subscription-id}/resourceGroups/agencia-rg \
     --sdk-auth
   ```

   b. Copiar el JSON resultante

   c. En GitHub:
   - Ve a tu repositorio → Settings → Secrets and variables → Actions
   - Click en "New repository secret"
   - Nombre: `AZURE_CREDENTIALS`
   - Valor: Pega el JSON del paso anterior

2. **Push a la rama main**
   ```bash
   git add .
   git commit -m "Add Azure deployment configuration"
   git push origin main
   ```

3. **Ver el progreso**
   - Ve a la pestaña "Actions" en GitHub
   - Verás el workflow ejecutándose

## 🧪 Pruebas Locales

### Probar Backend con Docker:

```bash
cd agencia-backend

# Build
docker build -t agencia-backend .

# Run
docker run -p 8080:8080 `
  -e MONGODB_URI="mongodb+srv://agencia_user:uagrm2025@agencia-database.8n7ayzu.mongodb.net/?appName=agencia-database" `
  -e JWT_SECRET="tu_clave_secreta_super_segura" `
  agencia-backend

# Probar
curl http://localhost:8080/actuator/health
```

### Probar Frontend con Docker:

```bash
cd agencia-frontend

# Build
docker build -t agencia-frontend .

# Run
docker run -p 80:80 agencia-frontend

# Abrir navegador
start http://localhost
```

## 📊 Verificar Deployment

### URLs de producción:

- **Frontend**: https://agencia-frontend-app.azurewebsites.net
- **Backend**: https://agencia-backend-app.azurewebsites.net
- **GraphQL**: https://agencia-backend-app.azurewebsites.net/graphql
- **GraphiQL**: https://agencia-backend-app.azurewebsites.net/graphiql

### Comandos útiles:

```bash
# Ver logs del backend
az webapp log tail --name agencia-backend-app --resource-group agencia-rg

# Ver logs del frontend
az webapp log tail --name agencia-frontend-app --resource-group agencia-rg

# Ver estado de las apps
az webapp show --name agencia-backend-app --resource-group agencia-rg --query state

# Reiniciar backend
az webapp restart --name agencia-backend-app --resource-group agencia-rg

# Reiniciar frontend
az webapp restart --name agencia-frontend-app --resource-group agencia-rg
```

## 🔄 Actualizar la Aplicación

### Desde PowerShell:

```powershell
# Backend
cd agencia-backend
docker build -t agenciaacr.azurecr.io/agencia-backend:latest .
az acr login --name agenciaacr
docker push agenciaacr.azurecr.io/agencia-backend:latest
az webapp restart --name agencia-backend-app --resource-group agencia-rg

# Frontend
cd agencia-frontend
docker build -t agenciaacr.azurecr.io/agencia-frontend:latest .
docker push agenciaacr.azurecr.io/agencia-frontend:latest
az webapp restart --name agencia-frontend-app --resource-group agencia-rg
```

### Desde GitHub:

```bash
git add .
git commit -m "Update application"
git push origin main
```

El workflow de GitHub Actions se ejecutará automáticamente.

## ⚙️ Variables de Entorno Configuradas

### Backend (agencia-backend-app):
- `MONGODB_URI`: Connection string de MongoDB Atlas
- `JWT_SECRET`: Clave secreta para JWT
- `SERVER_PORT`: 8080
- `CORS_ALLOWED_ORIGINS`: URL del frontend

### Frontend (agencia-frontend-app):
- `WEBSITES_PORT`: 80 (puerto de NGINX)
- `BACKEND_URL`: URL del backend

## 🔐 Seguridad

### Recomendaciones:

1. **Cambiar el JWT_SECRET** a un valor más seguro:
   ```bash
   az webapp config appsettings set \
     --resource-group agencia-rg \
     --name agencia-backend-app \
     --settings JWT_SECRET="tu_nueva_clave_super_segura_aleatoria_123456789"
   ```

2. **Configurar MongoDB Atlas**:
   - En MongoDB Atlas → Network Access
   - Añadir IP de Azure (o permitir 0.0.0.0/0 temporalmente)

3. **Habilitar HTTPS** (Azure lo hace automáticamente)

4. **Configurar dominio personalizado** (opcional):
   ```bash
   az webapp config hostname add \
     --webapp-name agencia-frontend-app \
     --resource-group agencia-rg \
     --hostname tudominio.com
   ```

## 🐛 Troubleshooting

### Problema: Backend no inicia

```bash
# Ver logs detallados
az webapp log tail --name agencia-backend-app --resource-group agencia-rg

# Verificar variables de entorno
az webapp config appsettings list --name agencia-backend-app --resource-group agencia-rg
```

### Problema: Frontend no puede conectar con Backend

1. Verificar que el backend esté corriendo
2. Revisar nginx.conf - debe apuntar a la URL correcta del backend
3. Verificar CORS en el backend

### Problema: Error de autenticación en MongoDB

1. Verificar que el connection string sea correcto
2. En MongoDB Atlas → Network Access → Permitir acceso desde Azure

## 🗑️ Eliminar todo (Cleanup)

Para eliminar todos los recursos y evitar costos:

```bash
az group delete --name agencia-rg --yes --no-wait
```

Esto eliminará:
- Container Registry
- App Service Plan
- Ambas Web Apps
- Todos los recursos asociados

## 📞 Soporte

Si tienes problemas:

1. Revisa los logs: `az webapp log tail`
2. Verifica el estado: `az webapp show`
3. Prueba localmente con Docker primero
4. Revisa la documentación de Azure: https://docs.microsoft.com/azure

## 🎉 ¡Listo!

Tu aplicación ahora está desplegada en Azure con:
- ✅ HTTPS automático
- ✅ Escalado automático (si es necesario)
- ✅ CI/CD con GitHub Actions
- ✅ Logs en tiempo real
- ✅ Alta disponibilidad
