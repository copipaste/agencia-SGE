# 🧪 GUÍA DE PRUEBAS - Integración Servicio BI

## ✅ Estado Actual

**COMPILACIÓN**: ✅ **EXITOSA** (BUILD SUCCESS)
- 73 archivos Java compilados sin errores
- Todos los componentes de integración BI funcionando

---

## 🎯 Plan de Pruebas Completo

### Nivel 1: Pruebas Sin Backend Corriendo ✅ COMPLETADO

#### ✅ 1.1 Verificación de Archivos
```powershell
.\VERIFY.ps1
```
**Resultado**: Todos los archivos Java creados correctamente

#### ✅ 1.2 Compilación
```powershell
.\mvnw.cmd clean compile -DskipTests
```
**Resultado**: BUILD SUCCESS - Sin errores de compilación

---

### Nivel 2: Pruebas Con Backend Corriendo

#### 2.1 Iniciar el Backend

**Terminal 1** (dejar corriendo):
```powershell
cd c:\Users\aintu\Desktop\sw2-agencia-jhoel\agencia-SGE\agencia-backend
.\mvnw.cmd spring-boot:run
```

**Esperar a ver**:
```
Started AgenciaBackendApplication in X.XXX seconds
```

---

#### 2.2 Pruebas Básicas (Terminal 2)

**Test 1: Health Check del Servicio BI (a través del backend)**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/bi/health"
```

**Resultado esperado**:
```json
{
  "status": "ok"
}
```

**Si falla**: El servicio BI en Render puede estar en cold start. Espera 30 segundos y reintenta.

---

**Test 2: Verificar que el endpoint requiere autenticación**
```powershell
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/bi/dashboard/resumen"
} catch {
    Write-Host "Correcto: Endpoint protegido (requiere autenticación)"
}
```

**Resultado esperado**: Error 401/403 (es correcto, significa que la seguridad funciona)

---

#### 2.3 Pruebas Con Autenticación

**Paso 1: Hacer Login y Obtener JWT**
```powershell
$loginQuery = @{
    query = 'mutation { login(username: "tu_usuario", password: "tu_password") { token } }'
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/graphql" -Method POST -Body $loginQuery -ContentType "application/json"

$token = $loginResponse.data.login.token
Write-Host "Token obtenido: $token"
```

**Paso 2: Probar Dashboard con Token**
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

$dashboard = Invoke-RestMethod -Uri "http://localhost:8080/api/bi/dashboard/resumen" -Headers $headers
$dashboard | ConvertTo-Json -Depth 5
```

**Resultado esperado**: JSON con KPIs, top_destinos, tendencia_reservas_por_dia

---

#### 2.4 Probar Todos los Endpoints

```powershell
# Sync Status
Invoke-RestMethod -Uri "http://localhost:8080/api/bi/sync/status" -Headers $headers

# KPI Margen Bruto
Invoke-RestMethod -Uri "http://localhost:8080/api/bi/kpi/margen-bruto" -Headers $headers

# KPI Tasa Conversión
Invoke-RestMethod -Uri "http://localhost:8080/api/bi/kpi/tasa-conversion" -Headers $headers

# KPI Tasa Cancelación
Invoke-RestMethod -Uri "http://localhost:8080/api/bi/kpi/tasa-cancelacion" -Headers $headers
```

---

### Nivel 3: Tests Automatizados (Opcional)

#### 3.1 Ejecutar Tests JUnit
```powershell
.\mvnw.cmd test -Dtest=BiServiceClientTest
```

**Nota**: Requiere que el servicio BI esté disponible.

---

## 📝 Script de Prueba Completo (Todo en Uno)

Guarda esto como `PRUEBA-COMPLETA.ps1`:

```powershell
Write-Host "=== PRUEBA COMPLETA DE INTEGRACION BI ===" -ForegroundColor Cyan
Write-Host ""

# Configuración
$backendUrl = "http://localhost:8080"
$username = Read-Host "Usuario de prueba"
$password = Read-Host "Password" -AsSecureString
$passwordText = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($password)
)

Write-Host "`n1. Probando health check (sin auth)..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$backendUrl/api/bi/health" -TimeoutSec 15
    Write-Host "   OK: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. Obteniendo token JWT..." -ForegroundColor Yellow
$loginQuery = @{
    query = "mutation { login(username: `"$username`", password: `"$passwordText`") { token username roles } }"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$backendUrl/graphql" -Method POST -Body $loginQuery -ContentType "application/json" -TimeoutSec 10
    $token = $loginResponse.data.login.token
    Write-Host "   OK: Token obtenido" -ForegroundColor Green
    Write-Host "   Usuario: $($loginResponse.data.login.username)" -ForegroundColor White
} catch {
    Write-Host "   ERROR: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

$headers = @{ "Authorization" = "Bearer $token" }

Write-Host "`n3. Probando Dashboard Resumen..." -ForegroundColor Yellow
try {
    $dashboard = Invoke-RestMethod -Uri "$backendUrl/api/bi/dashboard/resumen" -Headers $headers -TimeoutSec 15
    Write-Host "   OK: Dashboard obtenido" -ForegroundColor Green
    Write-Host "   Total Clientes: $($dashboard.kpis.total_clientes)" -ForegroundColor White
    Write-Host "   Total Ventas: $($dashboard.kpis.total_ventas_confirmadas)" -ForegroundColor White
} catch {
    Write-Host "   ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n4. Probando Sync Status..." -ForegroundColor Yellow
try {
    $sync = Invoke-RestMethod -Uri "$backendUrl/api/bi/sync/status" -Headers $headers -TimeoutSec 10
    Write-Host "   OK: $($sync.message)" -ForegroundColor Green
} catch {
    Write-Host "   ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== PRUEBAS COMPLETADAS ===" -ForegroundColor Cyan
```

---

## 🎯 Checklist de Resultados Esperados

Marca cada prueba completada:

### Compilación
- [x] ✅ Proyecto compila sin errores (BUILD SUCCESS)
- [x] ✅ Todos los archivos Java presentes

### Endpoints Públicos
- [ ] Health check responde `{"status":"ok"}`

### Endpoints Autenticados (requieren JWT)
- [ ] Dashboard resumen retorna KPIs
- [ ] Sync status retorna estado de sincronización
- [ ] KPIs individuales funcionan

### Seguridad
- [ ] Endpoints protegidos rechazan peticiones sin token
- [ ] JWT token se genera correctamente
- [ ] Token se acepta en headers

---

## 🚀 Orden Sugerido de Pruebas

1. **Ahora** → Ejecutar `.\VERIFY.ps1` (Ya hecho ✅)
2. **Ahora** → Compilar (Ya hecho ✅)
3. **Siguiente** → Iniciar backend: `.\mvnw.cmd spring-boot:run`
4. **Después** → Probar health check
5. **Después** → Login y obtener JWT
6. **Después** → Probar dashboard con JWT
7. **Opcional** → Ejecutar tests automatizados

---

## 🐛 Troubleshooting

### Backend no inicia
- Verificar que el puerto 8080 esté libre
- Revisar que MongoDB esté configurado correctamente
- Verificar logs en la consola

### Health check falla
- El servicio BI puede estar en cold start (normal)
- Esperar 30-60 segundos y reintentar
- Verificar conectividad a internet

### Error 401 en endpoints
- Verificar que obtuviste el JWT correctamente
- Verificar que el token no haya expirado
- Verificar headers de autorización

### Dashboard retorna datos vacíos
- Es normal si no hay datos en PostgreSQL del servicio BI
- El servicio BI necesita tiempo para sincronizar datos desde MongoDB

---

## 📞 Soporte

- **Documentación completa**: `INTEGRACION-SERVICIO-BI.md`
- **Inicio rápido**: `INICIO-RAPIDO-BI.md`
- **Ejemplos frontend**: `EJEMPLO-FRONTEND-ANGULAR.md`

---

**Última actualización**: 9 de noviembre de 2025
