# 📊 Integración Servicio de Business Intelligence

## 🎯 Descripción General

Este módulo implementa la integración completa entre el backend de Spring Boot y el microservicio de Business Intelligence desplegado en Render. La arquitectura sigue el patrón **backend-to-backend** donde el frontend **nunca** se comunica directamente con el servicio BI, manteniendo las credenciales seguras en el servidor.

---

## 📚 Índice de Documentación

### 🚀 Para Empezar Rápido
- **[INICIO-RAPIDO-BI.md](INICIO-RAPIDO-BI.md)** ← **EMPIEZA AQUÍ** (5 minutos)
  - Configuración básica
  - Primeras pruebas
  - Verificación de la integración

### ✅ Implementación Paso a Paso
- **[CHECKLIST-IMPLEMENTACION-BI.md](CHECKLIST-IMPLEMENTACION-BI.md)**
  - Checklist completo de todas las fases
  - Desde configuración hasta producción
  - Criterios de éxito

### 📖 Documentación Completa
- **[INTEGRACION-SERVICIO-BI.md](INTEGRACION-SERVICIO-BI.md)**
  - Documentación técnica detallada
  - Arquitectura y componentes
  - Troubleshooting completo
  - APIs y endpoints
  - Configuración avanzada

### 📝 Resumen Ejecutivo
- **[RESUMEN-INTEGRACION-BI.md](RESUMEN-INTEGRACION-BI.md)**
  - Resumen para stakeholders
  - Respuestas al equipo BI
  - Próximos pasos

### 💻 Ejemplos de Código
- **[EJEMPLO-FRONTEND-ANGULAR.md](EJEMPLO-FRONTEND-ANGULAR.md)**
  - Código completo Angular
  - Servicio TypeScript
  - Componente de Dashboard
  - Ejemplos con Chart.js

### 🧪 Testing
- **[TEST-BI-INTEGRATION.ps1](TEST-BI-INTEGRATION.ps1)**
  - Script PowerShell de pruebas
  - Validación automatizada
  - Tests interactivos

---

## ⚡ Inicio Rápido (2 minutos)

### 1. Configurar Token
```bash
# Crear archivo .env
Copy-Item .env.example .env

# Editar .env y añadir:
BI_AUTH_TOKEN=tu_token_secreto_aqui
```

### 2. Ejecutar Backend
```bash
.\mvnw.cmd spring-boot:run
```

### 3. Probar
```bash
# Ejecutar script de prueba
.\TEST-BI-INTEGRATION.ps1
```

¿Funciona? ✅ **Continúa con el frontend** ([ver ejemplos](EJEMPLO-FRONTEND-ANGULAR.md))

---

## 🏗️ Arquitectura

```
┌─────────────────┐
│                 │
│  Angular        │
│  Frontend       │
│                 │
└────────┬────────┘
         │ HTTP/REST (JWT)
         │ /api/bi/*
         ▼
┌─────────────────┐
│                 │
│  Spring Boot    │
│  Backend        │
│  (Proxy)        │
│                 │
└────────┬────────┘
         │ HTTP/REST (Bearer Token)
         │ BI_AUTH_TOKEN
         ▼
┌─────────────────┐
│                 │
│  Servicio BI    │
│  (Render)       │
│  Python/Docker  │
│                 │
└─────────────────┘
         │
         ▼
    PostgreSQL
```

---

## 📦 Componentes Implementados

### Backend (Spring Boot)

#### Configuración
- `BiServiceConfig.java` - Configuración del servicio BI
- `RestTemplateConfig.java` - Cliente HTTP con timeouts

#### DTOs (Data Transfer Objects)
- `HealthResponse.java`
- `SyncStatusResponse.java`
- `SyncRestartResponse.java`
- `DashboardResumenResponse.java`
- `KpiResponse.java`

#### Servicios
- `BiServiceClient.java` - Cliente HTTP con:
  - ✅ Reintentos automáticos (2 intentos)
  - ✅ Backoff exponencial
  - ✅ Timeout de 10 segundos
  - ✅ Logging detallado

#### Controladores
- `BiController.java` - API REST:
  - `GET /api/bi/health` - Health check (público)
  - `GET /api/bi/sync/status` - Estado de sync (auth)
  - `POST /api/bi/sync/restart` - Reiniciar sync (admin)
  - `GET /api/bi/dashboard/resumen` - Dashboard completo (auth)
  - `GET /api/bi/kpi/*` - KPIs individuales (auth)

#### Tests
- `BiServiceClientTest.java` - Suite de tests de integración

---

## 🌐 API Endpoints

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/api/bi/health` | ❌ No | Health check del servicio BI |
| GET | `/api/bi/sync/status` | ✅ Sí | Estado de sincronización en tiempo real |
| POST | `/api/bi/sync/restart` | 👑 Admin | Reiniciar sincronización (solo admins) |
| GET | `/api/bi/dashboard/resumen` | ✅ Sí | Dashboard completo con todos los KPIs |
| GET | `/api/bi/kpi/margen-bruto` | ✅ Sí | KPI: Margen bruto de ventas |
| GET | `/api/bi/kpi/tasa-conversion` | ✅ Sí | KPI: Tasa de conversión |
| GET | `/api/bi/kpi/tasa-cancelacion` | ✅ Sí | KPI: Tasa de cancelación |

### Ejemplo de Respuesta: Dashboard Resumen
```json
{
  "periodo": {
    "inicio": "2025-11-01",
    "fin": "2025-11-09"
  },
  "kpis": {
    "total_clientes": 123,
    "total_ventas_confirmadas": 45,
    "total_monto_vendido": 12345.67,
    "tasa_cancelacion": 2.34
  },
  "top_destinos": [
    { "destino": "Lima", "ingresos": 1234.56 }
  ],
  "tendencia_reservas_por_dia": [
    { "fecha": "2025-11-01", "cantidad_reservas": 5 }
  ]
}
```

---

## 🔐 Configuración de Seguridad

### Variables de Entorno

```properties
# REQUERIDO - Token para autenticación backend-to-backend
BI_AUTH_TOKEN=<token_secreto>

# Opcional - URL del servicio BI (tiene default)
BI_BASE_URL=https://sw2-servicio-bi.onrender.com

# Opcional - Configuración de timeouts y reintentos
BI_TIMEOUT=10000         # 10 segundos
BI_MAX_RETRIES=2         # 2 reintentos
BI_RETRY_DELAY=1000      # 1 segundo entre reintentos
```

### ⚠️ Reglas de Seguridad

1. **NUNCA** commitear `.env` con tokens reales
2. **NUNCA** exponer `BI_AUTH_TOKEN` al frontend
3. Configurar token en secretos de Azure/Render antes de desplegar
4. Rotar el token periódicamente
5. Usar variables de entorno en todos los ambientes

---

## 🧪 Testing

### Opción 1: Script PowerShell (Recomendado)
```powershell
.\TEST-BI-INTEGRATION.ps1
```

### Opción 2: Tests JUnit
```bash
.\mvnw.cmd test -Dtest=BiServiceClientTest
```

### Opción 3: cURL Manual
```bash
# Health check
curl http://localhost:8080/api/bi/health

# Dashboard (requiere JWT)
curl http://localhost:8080/api/bi/dashboard/resumen \
  -H "Authorization: Bearer <TU_JWT_TOKEN>"
```

---

## 🛠️ Configuración de Entornos

### Desarrollo Local
```bash
# 1. Copiar plantilla
Copy-Item .env.example .env

# 2. Editar .env
BI_AUTH_TOKEN=tu_token

# 3. Ejecutar
.\mvnw.cmd spring-boot:run
```

### Azure App Service
```bash
az webapp config appsettings set \
  --resource-group <grupo> \
  --name <app> \
  --settings BI_AUTH_TOKEN="<token>"
```

### Render
1. Dashboard → Environment
2. Add Environment Variable
3. Key: `BI_AUTH_TOKEN`
4. Value: `<token>`
5. Save Changes

---

## 🚀 Integración con Frontend

Ver **[EJEMPLO-FRONTEND-ANGULAR.md](EJEMPLO-FRONTEND-ANGULAR.md)** para:

- ✅ Servicio TypeScript completo
- ✅ Componente de Dashboard
- ✅ Template HTML con diseño
- ✅ Estilos CSS profesionales
- ✅ Integración con Chart.js
- ✅ Manejo de errores
- ✅ Loading states

---

## 📊 Características de Resiliencia

### Reintentos Automáticos
- **2 intentos** por defecto
- **Backoff exponencial**: 1s, 2s, 4s...
- Configurable vía `BI_MAX_RETRIES`

### Timeouts
- **10 segundos** por defecto
- Aplica a conexión y lectura
- Configurable vía `BI_TIMEOUT`

### Manejo de Errores
- Logging detallado
- Respuestas HTTP apropiadas
- No expone detalles internos al cliente

### Cold Start Handling
- Reintentos ayudan con cold starts de Render
- Logs indican si es problema temporal
- Timeout suficiente para arranque del servicio

---

## 📈 Monitoreo

### Logs a Revisar
```
DEBUG c.a.a.service.BiServiceClient : Llamando al servicio BI: /health (intento 1)
DEBUG c.a.a.service.BiServiceClient : Respuesta exitosa del servicio BI: /health
INFO  c.a.a.controller.BiController : Obteniendo resumen del dashboard BI
```

### Errores Comunes
```
WARN  c.a.a.service.BiServiceClient : Error en intento 1 para /dashboard/resumen
ERROR c.a.a.service.BiServiceClient : Fallo después de 2 intentos
```

### Métricas Recomendadas
- Tasa de éxito de llamadas al BI (objetivo: >95%)
- Tiempo promedio de respuesta (objetivo: <3s)
- Número de reintentos por endpoint
- Errores 5xx del servicio BI

---

## 🐛 Troubleshooting Rápido

### Error: "No se pudo conectar al servicio BI"
**Solución**:
```bash
# Verificar que el servicio BI está activo
curl https://sw2-servicio-bi.onrender.com/health

# Si responde, verificar configuración local
echo $env:BI_AUTH_TOKEN  # Debe tener un valor
```

### Error: "401 Unauthorized"
**Solución**:
- Verificar que `BI_AUTH_TOKEN` esté configurado
- Validar que el token sea correcto (contactar equipo BI)
- Revisar logs del backend para ver qué token se está enviando

### Respuestas lentas
**Causa**: Cold start de Render (servicio gratis)
**Solución**: 
- Es normal en el primer request
- Requests subsiguientes serán rápidos
- Considerar implementar cache de 30-60 segundos

Ver más en **[INTEGRACION-SERVICIO-BI.md](INTEGRACION-SERVICIO-BI.md#troubleshooting)**

---

## 📞 Soporte

### Documentación
- 📖 [Inicio Rápido](INICIO-RAPIDO-BI.md) - 5 minutos
- 📚 [Documentación Completa](INTEGRACION-SERVICIO-BI.md) - Todo
- ✅ [Checklist](CHECKLIST-IMPLEMENTACION-BI.md) - Paso a paso
- 💻 [Ejemplos Frontend](EJEMPLO-FRONTEND-ANGULAR.md) - Angular

### Contacto
- **Backend**: Issues en GitHub
- **Servicio BI**: Equipo de Business Intelligence
- **DevOps**: Para configuración de Azure/Render

---

## 🎓 Próximos Pasos

1. ✅ **Configurar credenciales** ([Inicio Rápido](INICIO-RAPIDO-BI.md))
2. ✅ **Probar integración** ([TEST-BI-INTEGRATION.ps1](TEST-BI-INTEGRATION.ps1))
3. 🔨 **Implementar frontend** ([Ejemplos Angular](EJEMPLO-FRONTEND-ANGULAR.md))
4. 🚀 **Desplegar a staging** ([Checklist](CHECKLIST-IMPLEMENTACION-BI.md))
5. ✅ **Validar con equipo BI**
6. 🚢 **Desplegar a producción**

---

## 📄 Licencia y Créditos

- **Proyecto**: Agencia de Viajes SGE
- **Microservicio BI**: Equipo de Business Intelligence
- **Integración Backend**: Implementada en Spring Boot 3.5.7
- **Fecha**: 9 de noviembre de 2025

---

**¿Listo para empezar?** 👉 [INICIO-RAPIDO-BI.md](INICIO-RAPIDO-BI.md)

**¿Tienes dudas?** 👉 [INTEGRACION-SERVICIO-BI.md](INTEGRACION-SERVICIO-BI.md)

**¿Necesitas ejemplos?** 👉 [EJEMPLO-FRONTEND-ANGULAR.md](EJEMPLO-FRONTEND-ANGULAR.md)
