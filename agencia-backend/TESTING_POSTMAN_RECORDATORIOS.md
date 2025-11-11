# 🧪 GUÍA DE TESTING: Envío Forzado de Recordatorios con Postman

**Fecha:** 11 de Noviembre, 2025  
**Objetivo:** Probar el envío manual de recordatorios

---

## 📋 OPCIONES DE TESTING

Tienes **3 formas** de probar el envío forzado:

1. ✅ **Opción 1:** Spring Boot GraphQL (simula lo que hará Angular)
2. ✅ **Opción 2:** FastAPI directo (más rápido para testing)
3. ✅ **Opción 3:** GraphQL Playground (visual)

---

## 🚀 OPCIÓN 1: SPRING BOOT GRAPHQL (RECOMENDADO)

**Esta es la forma en que Angular lo hará.**

### Paso 1: Obtener Token de Autenticación

**Endpoint:** `http://localhost:8080/graphql`  
**Método:** `POST`  
**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "query": "mutation { login(input: { email: \"agente@agencia.com\", password: \"Agente2024!\" }) { token type usuario { nombre isAgente } } }"
}
```

**Respuesta:**
```json
{
  "data": {
    "login": {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "type": "Bearer",
      "usuario": {
        "nombre": "Carlos",
        "isAgente": true
      }
    }
  }
}
```

**⚠️ Copia el token para el siguiente paso.**

---

### Paso 2: Ver Estadísticas (Antes del Envío)

**Endpoint:** `http://localhost:8080/graphql`  
**Método:** `POST`  
**Headers:**
```
Content-Type: application/json
Authorization: Bearer {tu_token_aqui}
```

**Body:**
```json
{
  "query": "query { estadisticasRecordatorios }"
}
```

**Respuesta Esperada:**
```json
{
  "data": {
    "estadisticasRecordatorios": {
      "success": true,
      "total_predicciones": 1,
      "recordatorios_pendientes": 1,
      "recordatorios_enviados": 0
    }
  }
}
```

---

### Paso 3: Forzar Envío de Recordatorios

**Endpoint:** `http://localhost:8080/graphql`  
**Método:** `POST`  
**Headers:**
```
Content-Type: application/json
Authorization: Bearer {tu_token_aqui}
```

**Body:**
```json
{
  "query": "mutation { forzarEnvioRecordatorios }"
}
```

**Respuesta Esperada:**
```json
{
  "data": {
    "forzarEnvioRecordatorios": {
      "success": true,
      "mensaje": "Recordatorios enviados correctamente",
      "detalles": {
        "success": true,
        "recordatorios_enviados": 1,
        "detalles": [
          {
            "venta_id": "691340e197fc4685fa3ef7a9",
            "email": "alan@gmail.com",
            "nombre": "Alan Romero",
            "paquete": "tour oruro",
            "destino": "oruro",
            "probabilidad": 0.8617,
            "resultado": "SIMULADO - Email enviado a alan@gmail.com"
          }
        ]
      }
    }
  }
}
```

---

### Paso 4: Ver Estadísticas (Después del Envío)

**Misma petición que el Paso 2.**

**Respuesta Esperada:**
```json
{
  "data": {
    "estadisticasRecordatorios": {
      "success": true,
      "total_predicciones": 1,
      "recordatorios_pendientes": 0,
      "recordatorios_enviados": 1
    }
  }
}
```

**✅ El contador de `recordatorios_enviados` debe haber aumentado.**

---

## ⚡ OPCIÓN 2: FASTAPI DIRECTO (MÁS RÁPIDO)

**Esta opción NO requiere autenticación y es más rápida para testing.**

### Ver Estadísticas

**Endpoint:** `http://localhost:8001/recordatorios/estadisticas`  
**Método:** `GET`  
**Headers:** Ninguno

**Respuesta:**
```json
{
  "success": true,
  "total_predicciones": 1,
  "recordatorios_pendientes": 1,
  "recordatorios_enviados": 0
}
```

---

### Forzar Envío

**Endpoint:** `http://localhost:8001/recordatorios/enviar`  
**Método:** `POST`  
**Headers:** Ninguno  
**Body:** Vacío

**Respuesta:**
```json
{
  "success": true,
  "recordatorios_enviados": 1,
  "detalles": [
    {
      "venta_id": "691340e197fc4685fa3ef7a9",
      "email": "alan@gmail.com",
      "nombre": "Alan Romero",
      "paquete": "tour oruro",
      "destino": "oruro",
      "monto": 600.0,
      "probabilidad": 0.8617,
      "fecha_venta": "2025-12-10",
      "resultado": "SIMULADO - Email enviado a alan@gmail.com"
    }
  ]
}
```

---

### Ver Alertas Pendientes

**Endpoint:** `http://localhost:8001/recordatorios/alertas`  
**Método:** `GET`  
**Headers:** Ninguno

**Respuesta (Antes del Envío):**
```json
{
  "success": true,
  "alertas": [
    {
      "venta_id": "691340e197fc4685fa3ef7a9",
      "email": "alan@gmail.com",
      "nombre": "Alan Romero",
      "paquete": "tour oruro",
      "destino": "oruro",
      "monto": 600.0,
      "probabilidad": 0.8617,
      "fecha_venta": "2025-12-10"
    }
  ]
}
```

**Respuesta (Después del Envío):**
```json
{
  "success": true,
  "alertas": []
}
```

---

## 🎨 OPCIÓN 3: GRAPHQL PLAYGROUND

### Paso 1: Abrir GraphQL Playground

**URL:** `http://localhost:8080/graphiql`

---

### Paso 2: Login

**En el área de Query:**
```graphql
mutation {
  login(input: {
    email: "agente@agencia.com"
    password: "Agente2024!"
  }) {
    token
    type
    usuario {
      nombre
      isAgente
    }
  }
}
```

**Click en ▶️ Play**

**Copia el token.**

---

### Paso 3: Configurar Headers

**En la sección "HTTP HEADERS" (abajo):**
```json
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Paso 4: Ver Estadísticas

```graphql
query {
  estadisticasRecordatorios
}
```

**Click en ▶️ Play**

---

### Paso 5: Enviar Recordatorios

```graphql
mutation {
  forzarEnvioRecordatorios
}
```

**Click en ▶️ Play**

---

## 🔍 VERIFICACIÓN EN LOGS

### Logs de Spring Boot:

```
🔔 GraphQL Mutation: forzarEnvioRecordatorios invocada
🔔 Forzando envío de recordatorios desde Angular...
✅ Recordatorios enviados exitosamente: {success=true, ...}
```

### Logs de FastAPI:

```
📊 Buscando alertas pendientes (sin filtro de fecha)...
✅ Alertas encontradas: 1
📧 SIMULADO: Enviando email a alan@gmail.com
   Asunto: Recordatorio de tu Reserva - tour oruro
   Fecha de viaje: 2025-12-10
   Probabilidad de cancelación: 86%
💾 Marcando como enviado en MongoDB...
✅ Recordatorios enviados: 1/1
```

---

## 📊 VERIFICACIÓN EN MONGODB

### Antes del Envío:

```javascript
db.predicciones_cancelacion.findOne({
  venta_id: "691340e197fc4685fa3ef7a9"
})

// Resultado:
{
  "_id": ObjectId("..."),
  "venta_id": "691340e197fc4685fa3ef7a9",
  "email_cliente": "alan@gmail.com",
  "recordatorio_enviado": false,  // ← false
  "fecha_envio_recordatorio": null
}
```

### Después del Envío:

```javascript
db.predicciones_cancelacion.findOne({
  venta_id: "691340e197fc4685fa3ef7a9"
})

// Resultado:
{
  "_id": ObjectId("..."),
  "venta_id": "691340e197fc4685fa3ef7a9",
  "email_cliente": "alan@gmail.com",
  "recordatorio_enviado": true,   // ← true
  "fecha_envio_recordatorio": ISODate("2025-11-11T...")
}
```

---

## ⚠️ ERRORES COMUNES

### Error 1: "Cannot invoke ... is null"

**Causa:** Spring Boot no reiniciado después de las modificaciones.

**Solución:**
```bash
# Detener Spring Boot (Ctrl+C)
mvn spring-boot:run
```

---

### Error 2: 403 Forbidden

**Causa:** Token inválido o usuario no es agente.

**Solución:**
- Verificar que el token es correcto
- Verificar que el usuario tiene `isAgente = true`
- Hacer login nuevamente

---

### Error 3: Connection refused (FastAPI)

**Causa:** FastAPI no está corriendo.

**Solución:**
```bash
cd "c:\Users\tengo\OneDrive\Documentos\Materias\sw2\2025\2do parcial\agencia\IAS\IA_predicción"
.\venv\Scripts\Activate.ps1
python main_v4.py
```

---

### Error 4: 0 recordatorios enviados (pero hay alertas)

**Causa:** Las alertas tienen `recordatorio_enviado = true` (ya fueron enviadas).

**Solución:**

**Opción A: Crear nueva reserva de alto riesgo**

**Opción B: Resetear la alerta en MongoDB:**
```javascript
db.predicciones_cancelacion.updateOne(
  { venta_id: "691340e197fc4685fa3ef7a9" },
  { 
    $set: { 
      recordatorio_enviado: false,
      fecha_envio_recordatorio: null
    }
  }
)
```

---

## 📋 CHECKLIST DE TESTING

### Preparación:
- [ ] Spring Boot corriendo en puerto 8080
- [ ] FastAPI corriendo en puerto 8001
- [ ] MongoDB accesible
- [ ] Existe al menos 1 alerta con probabilidad >= 70%

### Testing con Postman (Opción 1):
- [ ] Login exitoso
- [ ] Token obtenido
- [ ] Estadísticas muestran alertas pendientes
- [ ] Mutation ejecutada exitosamente
- [ ] Estadísticas actualizadas (pendientes decrementaron)

### Testing con FastAPI (Opción 2):
- [ ] GET /recordatorios/estadisticas funciona
- [ ] POST /recordatorios/enviar funciona
- [ ] Response muestra detalles de envío
- [ ] Logs de FastAPI muestran "SIMULADO - Email enviado"

### Verificación:
- [ ] Logs de Spring Boot OK
- [ ] Logs de FastAPI OK
- [ ] MongoDB actualizado (recordatorio_enviado = true)

---

## 🎯 RESULTADO EXITOSO

Si todo funciona correctamente, deberías ver:

1. ✅ **Postman:** Response con `success: true` y detalles de envío
2. ✅ **Spring Boot Logs:** "✅ Recordatorios enviados exitosamente"
3. ✅ **FastAPI Logs:** "✅ Recordatorios enviados: 1/1"
4. ✅ **MongoDB:** Campo `recordatorio_enviado` cambia a `true`
5. ✅ **Estadísticas:** `recordatorios_pendientes` disminuye, `recordatorios_enviados` aumenta

---

## 📝 NOTAS FINALES

### Modo Simulación:

Actualmente FastAPI está en **modo simulación**:
- ✅ Ejecuta toda la lógica
- ✅ Actualiza MongoDB
- ✅ Genera logs detallados
- ❌ NO envía emails reales
- ✅ Logs dicen "SIMULADO - Email enviado"

### Para Producción:

Configurar SMTP en FastAPI (`.env`):
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=tu_email@gmail.com
SMTP_PASSWORD=tu_password_app
```

Luego reiniciar FastAPI y los emails se enviarán realmente.

---

**¿Listo para probar?** Elige la opción que prefieras y sigue los pasos. 🚀

