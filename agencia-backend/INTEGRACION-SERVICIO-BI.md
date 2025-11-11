# Integración del Servicio de Business Intelligence

## 📋 Resumen

Este documento describe la integración completa del microservicio de Business Intelligence (BI) desplegado en Render con el backend de Spring Boot. La arquitectura implementada sigue el patrón **backend-to-backend**, donde:

- ✅ El frontend **NO** se comunica directamente con el servicio BI
- ✅ El backend actúa como **proxy seguro** entre el frontend y el servicio BI
- ✅ Las credenciales (tokens) se mantienen en el servidor, **nunca** en el navegador

## 🏗️ Arquitectura Implementada

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────────┐
│             │  REST   │                  │  REST   │                 │
│  Frontend   │ ──────> │  Spring Boot     │ ──────> │  Servicio BI    │
│  (Angular)  │         │  Backend         │         │  (Render)       │
│             │ <────── │  (Proxy)         │ <────── │                 │
└─────────────┘         └──────────────────┘         └─────────────────┘
                             Endpoints:                 Endpoints:
                             /api/bi/*                  /health
                                                       /sync/status
                                                       /dashboard/resumen
                                                       /kpi/*
```

## 📦 Componentes Creados

### 1. Configuración (`config/`)
- **`BiServiceConfig.java`**: Configuración centralizada del servicio BI
  - URL base del servicio
  - Token de autenticación
  - Timeouts y reintentos
  
- **`RestTemplateConfig.java`**: Configuración de RestTemplate con timeouts

### 2. DTOs (`dto/bi/`)
- **`HealthResponse.java`**: Respuesta del health check
- **`SyncStatusResponse.java`**: Estado de sincronización
- **`SyncRestartResponse.java`**: Respuesta al reiniciar sincronización
- **`DashboardResumenResponse.java`**: Datos del dashboard (KPIs, destinos, tendencias)
- **`KpiResponse.java`**: Respuesta genérica de KPIs

### 3. Servicios (`service/`)
- **`BiServiceClient.java`**: Cliente HTTP para comunicación con el servicio BI
  - Reintentos automáticos con backoff exponencial
  - Manejo de timeouts
  - Logging detallado
  - Inyección de token Bearer en headers

### 4. Controladores (`controller/`)
- **`BiController.java`**: API REST para el frontend
  - Endpoints protegidos con autenticación
  - Control de acceso por roles
  - Manejo de errores

## 🔐 Configuración de Seguridad

### Variables de Entorno Requeridas

**Para desarrollo local**, crear/editar el archivo `.env` o configurar en el IDE:

```properties
# Token de autenticación para el servicio BI (REQUERIDO)
BI_AUTH_TOKEN=tu_token_secreto_compartido_con_el_equipo_bi

# URL del servicio BI (opcional, tiene valor por defecto)
BI_BASE_URL=https://sw2-servicio-bi.onrender.com
```

**Para despliegue en producción** (Azure, Render, etc.):

1. **Azure App Service**:
   ```bash
   az webapp config appsettings set \
     --resource-group tu-grupo \
     --name tu-app \
     --settings BI_AUTH_TOKEN="tu_token_secreto"
   ```

2. **Render**:
   - Dashboard → Environment → Add Environment Variable
   - Key: `BI_AUTH_TOKEN`
   - Value: `tu_token_secreto`

3. **Docker**:
   ```bash
   docker run -e BI_AUTH_TOKEN="tu_token_secreto" tu-imagen
   ```

### ⚠️ IMPORTANTE: Seguridad del Token

- ❌ **NUNCA** commitear el token en Git
- ❌ **NUNCA** exponer el token al frontend
- ✅ Usar variables de entorno
- ✅ Almacenar en secretos del sistema de CI/CD
- ✅ Rotar periódicamente

## 🚀 Endpoints Disponibles

### 1. Health Check (público)
```
GET /api/bi/health
```
**Respuesta**:
```json
{
  "status": "ok"
}
```

### 2. Estado de Sincronización (autenticado)
```
GET /api/bi/sync/status
Authorization: Bearer {jwt_token}
```
**Respuesta**:
```json
{
  "sync_enabled": true,
  "sync_running": true,
  "message": "Sincronización en tiempo real activa"
}
```

### 3. Reiniciar Sincronización (solo ADMIN)
```
POST /api/bi/sync/restart
Authorization: Bearer {jwt_token}
```
**Respuesta**:
```json
{
  "status": "success",
  "message": "Sincronización reiniciada exitosamente"
}
```

### 4. Dashboard Resumen (autenticado)
```
GET /api/bi/dashboard/resumen
Authorization: Bearer {jwt_token}
```
**Respuesta**:
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
    {
      "destino": "Lima",
      "ingresos": 1234.56
    }
  ],
  "tendencia_reservas_por_dia": [
    {
      "fecha": "2025-11-01",
      "cantidad_reservas": 5
    }
  ]
}
```

### 5. KPIs Individuales (autenticados)

**Margen Bruto**:
```
GET /api/bi/kpi/margen-bruto
Authorization: Bearer {jwt_token}
```

**Tasa de Conversión**:
```
GET /api/bi/kpi/tasa-conversion
Authorization: Bearer {jwt_token}
```

**Tasa de Cancelación**:
```
GET /api/bi/kpi/tasa-cancelacion
Authorization: Bearer {jwt_token}
```

**Respuesta típica**:
```json
{
  "nombre": "Margen Bruto",
  "valor": 15.5,
  "periodo_inicio": "2025-11-01",
  "periodo_fin": "2025-11-09",
  "unidad": "%",
  "descripcion": "Margen de ganancia sobre ventas"
}
```

## 🧪 Pruebas Locales

### Opción 1: Usando cURL

**1. Health Check (sin autenticación)**:
```bash
curl http://localhost:8080/api/bi/health
```

**2. Dashboard (con autenticación)**:
```bash
# Primero obtener un token JWT haciendo login
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { login(username:\"admin\", password:\"tu_password\") { token } }"}'

# Usar el token obtenido
curl http://localhost:8080/api/bi/dashboard/resumen \
  -H "Authorization: Bearer TU_TOKEN_JWT_AQUI"
```

**3. Reiniciar sincronización (requiere rol ADMIN)**:
```bash
curl -X POST http://localhost:8080/api/bi/sync/restart \
  -H "Authorization: Bearer TU_TOKEN_JWT_ADMIN"
```

### Opción 2: Usando el script de prueba

Ejecutar el script PowerShell incluido:
```powershell
.\agencia-backend\TEST-BI-INTEGRATION.ps1
```

### Opción 3: Desde el código Java

Revisar y ejecutar:
```
agencia-backend\src\test\java\com\agencia\agencia_backend\BiServiceClientTest.java
```

## 📊 Características de Resiliencia

### 1. Reintentos Automáticos
- **Número de reintentos**: 2 (configurable vía `BI_MAX_RETRIES`)
- **Backoff**: Exponencial (1s, 2s, 4s...)
- **Configurable**: `bi.service.retry-delay=1000`

### 2. Timeouts
- **Timeout por defecto**: 10 segundos
- **Configurable**: `bi.service.timeout=10000`
- Aplica tanto a conexión como a lectura

### 3. Manejo de Errores
- Logging detallado de intentos fallidos
- Respuestas HTTP apropiadas al frontend
- No exponer detalles internos al cliente

### 4. Circuit Breaker (Recomendación Futura)
Para mayor resiliencia, considerar añadir:
```xml
<!-- En pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

## 🔍 Monitoreo y Logs

### Logs a Revisar

Los logs del backend mostrarán:

```
DEBUG c.a.a.service.BiServiceClient : Llamando al servicio BI: https://sw2-servicio-bi.onrender.com/health (intento 1)
DEBUG c.a.a.service.BiServiceClient : Respuesta exitosa del servicio BI: /health
INFO  c.a.a.controller.BiController : Obteniendo resumen del dashboard BI
```

En caso de error:
```
WARN  c.a.a.service.BiServiceClient : Error en intento 1 para /dashboard/resumen: Connection timeout
ERROR c.a.a.service.BiServiceClient : Fallo después de 2 intentos para /dashboard/resumen
```

### Métricas Sugeridas

Implementar en el futuro:
- Tasa de éxito/fallo de llamadas al servicio BI
- Tiempo promedio de respuesta
- Número de reintentos por endpoint
- Disponibilidad del servicio BI

## 🚦 Checklist de Despliegue

Antes de desplegar a producción:

- [ ] Configurar `BI_AUTH_TOKEN` en variables de entorno
- [ ] Verificar que la URL del servicio BI es correcta
- [ ] Probar health check desde el servidor de producción
- [ ] Validar que los timeouts son apropiados para la red
- [ ] Configurar logs en nivel INFO (no DEBUG) en producción
- [ ] Documentar el token en el gestor de secretos del equipo
- [ ] Coordinar con el equipo BI para validación end-to-end
- [ ] Configurar alertas para fallos de integración
- [ ] Actualizar documentación del frontend con los nuevos endpoints

## 🐛 Troubleshooting

### Error: "No se pudo conectar al servicio BI"

**Causa**: Timeout o servicio BI no disponible

**Solución**:
1. Verificar que el servicio BI esté activo: `curl https://sw2-servicio-bi.onrender.com/health`
2. Aumentar timeout: `BI_TIMEOUT=15000`
3. Revisar logs del servicio BI en Render

### Error: "401 Unauthorized"

**Causa**: Token de autenticación incorrecto o no configurado

**Solución**:
1. Verificar que `BI_AUTH_TOKEN` esté configurado
2. Validar que el token coincide con el del servicio BI
3. Revisar que el header Authorization se está enviando correctamente

### Error: "403 Forbidden"

**Causa**: Usuario sin permisos suficientes

**Solución**:
1. Para `/sync/restart`: Requiere rol ADMIN
2. Verificar roles del usuario autenticado
3. Revisar configuración de Spring Security

### Respuestas lentas

**Causa**: Servicio BI en "cold start" (Render free tier)

**Solución**:
1. Implementar cache local de resultados (ej: 30 segundos)
2. Considerar hacer llamadas periódicas para mantener el servicio activo
3. Actualizar a plan de pago de Render si el problema persiste

## 📚 Recursos Adicionales

- [Documentación del Servicio BI](https://sw2-servicio-bi.onrender.com/docs) (si está disponible)
- [Spring RestTemplate](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate)
- [Spring Boot Configuration Properties](https://docs.spring.io/spring-boot/reference/features/external-config.html)

## 👥 Contacto

Para dudas o problemas con la integración:
- **Backend**: Equipo agencia-SGE
- **Servicio BI**: Equipo de Business Intelligence
- **Coordinación**: Crear issue en el repositorio

---

**Última actualización**: 9 de noviembre de 2025
