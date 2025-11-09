# 🚀 Guía de Actualización - Deploy a Azure

Esta guía te ayudará a subir cambios tanto del **backend** como del **frontend** a Azure después de hacer modificaciones en el código.

---

## 📋 Requisitos Previos

Asegúrate de tener:
- ✅ Azure CLI instalado y autenticado
- ✅ Docker Desktop ejecutándose
- ✅ Acceso al Container Registry de Azure

---

## 🔐 PASO 0: Login en Azure (Solo si no estás logueado)

```powershell
# Verificar si estás logueado
az account show

# Si no estás logueado, ejecutar:
az login

# Login al Container Registry
az acr login --name agencia
```

---

## 🔧 ACTUALIZAR BACKEND (Spring Boot)

### Paso 1: Navegar a la carpeta del backend
```powershell
cd agencia-backend
```

### Paso 2: Construir la nueva imagen Docker
```powershell
docker build -t agencia.azurecr.io/agencia-backend:latest .
```
⏱️ **Tiempo estimado**: 2-3 minutos

### Paso 3: Subir la imagen al Container Registry
```powershell
docker push agencia.azurecr.io/agencia-backend:latest
```
⏱️ **Tiempo estimado**: 1-2 minutos

### Paso 4: Reiniciar la Web App del backend
```powershell
az webapp restart --name agencia-backend-app --resource-group agencia
```
⏱️ **Tiempo estimado**: 30 segundos

### Paso 5: Verificar que funciona
```powershell
# Volver a la raíz del proyecto
cd ..

# Probar el endpoint
$loginQuery = @{ query = 'mutation { login(input: { email: "agente@agencia.com", password: "agente123" }) { token type usuario { email nombre } } }' } | ConvertTo-Json
Invoke-RestMethod -Uri "https://agencia-backend-app.azurewebsites.net/graphql" -Method POST -Body $loginQuery -ContentType "application/json"
```

✅ **Si obtienes un token, el backend está funcionando correctamente**

---

## 🎨 ACTUALIZAR FRONTEND (Angular)

### Paso 1: Navegar a la carpeta del frontend
```powershell
cd agencia-frontend
```

### Paso 2: Construir la nueva imagen Docker
```powershell
docker build -t agencia.azurecr.io/agencia-frontend:latest .
```
⏱️ **Tiempo estimado**: 1-2 minutos

### Paso 3: Subir la imagen al Container Registry
```powershell
docker push agencia.azurecr.io/agencia-frontend:latest
```
⏱️ **Tiempo estimado**: 1 minuto

### Paso 4: Actualizar la configuración del contenedor
```powershell
az webapp config container set --name agencia-frontend-app --resource-group agencia --docker-custom-image-name agencia.azurecr.io/agencia-frontend:latest
```

### Paso 5: Reiniciar la Web App del frontend
```powershell
az webapp restart --name agencia-frontend-app --resource-group agencia
```
⏱️ **Tiempo estimado**: 30 segundos

### Paso 6: Volver a la raíz del proyecto
```powershell
cd ..
```

### Paso 7: Verificar que funciona
Espera **1-2 minutos** y luego abre:
```
https://agencia-frontend-app.azurewebsites.net
```

✅ **Si la aplicación carga y puedes hacer login, el frontend está funcionando correctamente**

---

## 🔄 ACTUALIZAR AMBOS (Backend + Frontend)

Si hiciste cambios en ambos proyectos, puedes ejecutar todos los comandos seguidos:

```powershell
# 1. BACKEND
cd agencia-backend
docker build -t agencia.azurecr.io/agencia-backend:latest .
docker push agencia.azurecr.io/agencia-backend:latest
az webapp restart --name agencia-backend-app --resource-group agencia
cd ..

# 2. FRONTEND
cd agencia-frontend
docker build -t agencia.azurecr.io/agencia-frontend:latest .
docker push agencia.azurecr.io/agencia-frontend:latest
az webapp config container set --name agencia-frontend-app --resource-group agencia --docker-custom-image-name agencia.azurecr.io/agencia-frontend:latest
az webapp restart --name agencia-frontend-app --resource-group agencia
cd ..

Write-Host "✅ Despliegue completado. Espera 1-2 minutos para que los cambios se apliquen." -ForegroundColor Green
```

⏱️ **Tiempo total estimado**: 5-7 minutos

---

## 📝 CASOS COMUNES

### Cambios solo en código Java (Backend)
```powershell
cd agencia-backend
docker build -t agencia.azurecr.io/agencia-backend:latest .
docker push agencia.azurecr.io/agencia-backend:latest
az webapp restart --name agencia-backend-app --resource-group agencia
cd ..
```

### Cambios solo en código TypeScript/HTML/CSS (Frontend)
```powershell
cd agencia-frontend
docker build -t agencia.azurecr.io/agencia-frontend:latest .
docker push agencia.azurecr.io/agencia-frontend:latest
az webapp restart --name agencia-frontend-app --resource-group agencia
cd ..
```

### Cambios en GraphQL Schema (Backend)
```powershell
cd agencia-backend
docker build -t agencia.azurecr.io/agencia-backend:latest .
docker push agencia.azurecr.io/agencia-backend:latest
az webapp restart --name agencia-backend-app --resource-group agencia
cd ..
```

### Cambios en configuración de GraphQL (Frontend)
**Ejemplo:** Si cambias `graphql.module.ts`
```powershell
cd agencia-frontend
docker build -t agencia.azurecr.io/agencia-frontend:latest .
docker push agencia.azurecr.io/agencia-frontend:latest
az webapp restart --name agencia-frontend-app --resource-group agencia
cd ..
```

---

## 🐛 TROUBLESHOOTING

### Problema: "Login failed" al hacer `az acr login`
**Solución:**
```powershell
az login
az acr login --name agencia
```

### Problema: "No space left on device" al construir Docker
**Solución:**
```powershell
# Limpiar imágenes antiguas
docker system prune -a

# Luego volver a construir
```

### Problema: La aplicación no refleja los cambios
**Solución:**
```powershell
# Opción 1: Forzar actualización del contenedor
az webapp config container set --name agencia-backend-app --resource-group agencia --docker-custom-image-name agencia.azurecr.io/agencia-backend:latest
az webapp restart --name agencia-backend-app --resource-group agencia

# Opción 2: Ver logs para diagnosticar
az webapp log tail --name agencia-backend-app --resource-group agencia
```

### Problema: Error 503 "Service Unavailable"
**Solución:**
- Espera 2-3 minutos después del restart
- El contenedor puede tardar en iniciar
- Verifica los logs con `az webapp log tail`

---

## 📊 Ver Logs en Tiempo Real

### Backend
```powershell
az webapp log tail --name agencia-backend-app --resource-group agencia
```

### Frontend
```powershell
az webapp log tail --name agencia-frontend-app --resource-group agencia
```

**Presiona `Ctrl+C` para salir de los logs**

---

## 🔗 URLs Útiles

- **Frontend**: https://agencia-frontend-app.azurewebsites.net
- **Backend API**: https://agencia-backend-app.azurewebsites.net
- **GraphQL Playground**: https://agencia-backend-app.azurewebsites.net/graphql
- **GraphiQL Interface**: https://agencia-backend-app.azurewebsites.net/graphiql
- **Health Check**: https://agencia-backend-app.azurewebsites.net/actuator/health
- **Azure Portal**: https://portal.azure.com

---

## ⚡ Script Rápido de Actualización

Crea un archivo `deploy.ps1` en la raíz del proyecto:

```powershell
# deploy.ps1
param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("backend", "frontend", "both")]
    [string]$Target = "both"
)

Write-Host "🚀 Iniciando despliegue..." -ForegroundColor Cyan

if ($Target -eq "backend" -or $Target -eq "both") {
    Write-Host "`n📦 Actualizando BACKEND..." -ForegroundColor Yellow
    cd agencia-backend
    docker build -t agencia.azurecr.io/agencia-backend:latest .
    docker push agencia.azurecr.io/agencia-backend:latest
    az webapp restart --name agencia-backend-app --resource-group agencia
    cd ..
    Write-Host "✅ Backend actualizado" -ForegroundColor Green
}

if ($Target -eq "frontend" -or $Target -eq "both") {
    Write-Host "`n🎨 Actualizando FRONTEND..." -ForegroundColor Yellow
    cd agencia-frontend
    docker build -t agencia.azurecr.io/agencia-frontend:latest .
    docker push agencia.azurecr.io/agencia-frontend:latest
    az webapp config container set --name agencia-frontend-app --resource-group agencia --docker-custom-image-name agencia.azurecr.io/agencia-frontend:latest
    az webapp restart --name agencia-frontend-app --resource-group agencia
    cd ..
    Write-Host "✅ Frontend actualizado" -ForegroundColor Green
}

Write-Host "`n🎉 Despliegue completado. Espera 1-2 minutos para que los cambios se apliquen." -ForegroundColor Green
Write-Host "🌐 Frontend: https://agencia-frontend-app.azurewebsites.net" -ForegroundColor Cyan
Write-Host "⚙️  Backend: https://agencia-backend-app.azurewebsites.net/graphql" -ForegroundColor Cyan
```

### Uso del script:
```powershell
# Actualizar solo backend
.\deploy.ps1 -Target backend

# Actualizar solo frontend
.\deploy.ps1 -Target frontend

# Actualizar ambos
.\deploy.ps1 -Target both
# o simplemente
.\deploy.ps1
```

---

## 🎯 Checklist Antes de Desplegar

### Backend:
- [ ] Código compilando sin errores localmente
- [ ] Tests pasando (si los tienes)
- [ ] Variables de entorno correctas en Azure
- [ ] Dockerfile actualizado si cambiaste dependencias

### Frontend:
- [ ] Aplicación construyendo sin errores localmente
- [ ] URL del backend apuntando a Azure (`graphql.module.ts`)
- [ ] `nginx.conf` configurado correctamente
- [ ] Dockerfile actualizado si cambiaste dependencias

---

## 💰 Costos de las Actualizaciones

- **Build de imágenes**: Gratis (local)
- **Push al Container Registry**: Gratis (incluido en plan Basic)
- **Restart de Web Apps**: Gratis
- **Tiempo de inactividad**: ~30 segundos durante restart

---

## 📚 Referencias Rápidas

### Comandos Azure CLI más usados:
```powershell
# Ver estado de las apps
az webapp list --resource-group agencia --output table

# Ver configuración de una app
az webapp config show --name agencia-backend-app --resource-group agencia

# Ver variables de entorno
az webapp config appsettings list --name agencia-backend-app --resource-group agencia

# Abrir app en navegador
az webapp browse --name agencia-frontend-app --resource-group agencia
```

### Comandos Docker más usados:
```powershell
# Ver imágenes locales
docker images

# Ver contenedores corriendo
docker ps

# Limpiar sistema
docker system prune -a

# Ver logs de un contenedor local
docker logs <container-id>
```

---

¡Listo! Con esta guía podrás actualizar tu aplicación en Azure cada vez que hagas cambios. 🚀

**Tip:** Guarda este archivo en la raíz de tu proyecto para tenerlo siempre a mano.
