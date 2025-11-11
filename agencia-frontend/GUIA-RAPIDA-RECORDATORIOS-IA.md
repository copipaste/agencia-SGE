# 🚀 GUÍA RÁPIDA: Integración Recordatorios IA - Angular

**Fecha:** 11 de Noviembre, 2025  
**Estado:** ✅ Completado en Frontend

---

## 📌 RESUMEN

Se implementó un **botón de recordatorios inteligentes** en Angular que permite a los agentes enviar manualmente recordatorios de cancelación a clientes con alta probabilidad de cancelar.

**Ubicación:** `/dashboard/ventas` (Gestión de Ventas)

---

## 🎯 LO QUE SE IMPLEMENTÓ

### 1. Nuevo Servicio: `ia.service.ts`

**Path:** `src/app/services/ia.service.ts`

**Métodos:**
```typescript
forzarEnvioRecordatorios(): Observable<any>
obtenerEstadisticasRecordatorios(): Observable<any>
```

**Comunicación:** GraphQL → Spring Boot → FastAPI

---

### 2. Botón en Ventas: `venta-list.component`

**Archivos Modificados:**
- `venta-list.component.ts` - Lógica
- `venta-list.component.html` - UI
- `venta-list.component.css` - Estilos

**Funcionalidades Añadidas:**
- ✅ Botón "🤖 Recordatorios IA"
- ✅ Panel de estadísticas colapsable
- ✅ Manejo de errores robusto
- ✅ Estados de carga
- ✅ Confirmación antes de enviar

---

## 🔗 FLUJO DE COMUNICACIÓN

```
Angular (Frontend)
   │
   ├─ IAService.forzarEnvioRecordatorios()
   │
   └─► GraphQL Mutation ───► Spring Boot (Backend)
                                  │
                                  ├─ IAResolver
                                  ├─ RecordatorioService  
                                  │
                                  └─► HTTP POST ───► FastAPI (Microservicio IA)
                                                         │
                                                         ├─ MongoDB Query
                                                         ├─ Filtrar prob > 0.7
                                                         ├─ Enviar emails
                                                         └─► Respuesta JSON
```

---

## 📡 ENDPOINTS GRAPHQL REQUERIDOS EN SPRING BOOT

### Mutation: Enviar Recordatorios

**GraphQL:**
```graphql
mutation {
  forzarEnvioRecordatorios
}
```

**Debe Retornar:**
```json
{
  "success": true,
  "mensaje": "Recordatorios enviados correctamente",
  "detalles": {
    "recordatorios_enviados": 3,
    "detalles": [
      {
        "venta_id": "abc123",
        "email": "cliente@email.com",
        "nombre": "Juan Pérez",
        "paquete": "Tour Cancún",
        "probabilidad": 0.85
      }
    ]
  }
}
```

### Query: Estadísticas

**GraphQL:**
```graphql
query {
  estadisticasRecordatorios
}
```

**Debe Retornar:**
```json
{
  "success": true,
  "total_predicciones": 10,
  "recordatorios_pendientes": 3,
  "recordatorios_enviados": 7
}
```

---

## 🔒 SEGURIDAD

**Restricción:** Solo usuarios con rol `AGENTE` pueden ejecutar.

**Validación en Spring Boot:**
```java
@PreAuthorize("hasRole('AGENTE')")
```

Si un cliente intenta ejecutar → Error 403 Forbidden

---

## 🧪 TESTING RÁPIDO

### Paso 1: Levantar Servicios

```bash
# Terminal 1: Angular
ng serve

# Terminal 2: Spring Boot
mvn spring-boot:run

# Terminal 3: FastAPI
uvicorn main:app --reload --port 8001
```

### Paso 2: Login como Agente

```
URL: http://localhost:4200/login
Email: agente@agencia.com
Password: Agente2024!
```

### Paso 3: Ir a Ventas

```
URL: http://localhost:4200/dashboard/ventas
```

### Paso 4: Verificar

✅ Botón "🤖 Recordatorios IA" visible  
✅ Panel de estadísticas debajo del header  
✅ Clic en botón → Confirmación → Envío  
✅ Alerta con resultado detallado  

---

## ⚠️ ERRORES COMUNES

### Error 1: "IAResolver not found"

**Causa:** Spring Boot no tiene el resolver GraphQL implementado

**Solución:** Backend debe crear:
- `IAResolver.java`
- Actualizar `schema.graphqls`

---

### Error 2: "Access Denied"

**Causa:** Usuario no es agente

**Solución:** Hacer login con cuenta de agente

---

### Error 3: "Network Error"

**Causa:** Spring Boot o FastAPI no están corriendo

**Solución:** 
- Verificar `http://localhost:8080/graphql`
- Verificar `http://localhost:8001/docs`

---

## 📋 CHECKLIST PARA BACKEND

### Spring Boot Debe Implementar:

- [ ] `IAResolver.java` con mutations/queries
- [ ] `RecordatorioService.java` con lógica HTTP
- [ ] `schema.graphqls` con definiciones
- [ ] `@PreAuthorize("hasRole('AGENTE')")`
- [ ] Conexión HTTP a FastAPI en puerto 8001

**Ejemplo IAResolver.java:**
```java
@Controller
public class IAResolver {
    
    @Autowired
    private RecordatorioService recordatorioService;
    
    @PreAuthorize("hasRole('AGENTE')")
    @MutationMapping
    public Map<String, Object> forzarEnvioRecordatorios() {
        return recordatorioService.forzarEnvioRecordatorios();
    }
    
    @PreAuthorize("hasRole('AGENTE')")
    @QueryMapping
    public Map<String, Object> estadisticasRecordatorios() {
        return recordatorioService.obtenerEstadisticasRecordatorios();
    }
}
```

---

### FastAPI Debe Tener:

- [ ] `POST /recordatorios/enviar`
- [ ] `GET /recordatorios/estadisticas`
- [ ] Conexión a MongoDB
- [ ] Sistema de envío de emails
- [ ] Filtro de probabilidad > 0.7

**Ejemplo FastAPI Endpoint:**
```python
@app.post("/recordatorios/enviar")
async def enviar_recordatorios():
    # 1. Obtener predicciones de MongoDB
    predicciones = db.predicciones_cancelacion.find({
        "probabilidad_cancelacion": {"$gt": 0.7},
        "recordatorio_enviado": False
    })
    
    # 2. Enviar emails
    enviados = []
    for pred in predicciones:
        resultado = enviar_email(pred)
        enviados.append(resultado)
        
        # 3. Marcar como enviado
        db.predicciones_cancelacion.update_one(
            {"_id": pred["_id"]},
            {"$set": {"recordatorio_enviado": True}}
        )
    
    # 4. Retornar resultado
    return {
        "success": True,
        "recordatorios_enviados": len(enviados),
        "detalles": enviados
    }
```

---

## 🎨 CARACTERÍSTICAS DE UI

### Botón de Recordatorios

**Estilo:** Gradiente azul cian  
**Icono:** 🤖  
**Estados:**
- Normal: "Recordatorios IA"
- Cargando: "Enviando..." (disabled)
- Hover: Elevación con sombra

### Panel de Estadísticas

**Ubicación:** Debajo del header, encima de la tabla  
**Tipo:** Colapsable (toggle)  
**Cards:**
- 🎯 Total Predicciones
- ⏳ Recordatorios Pendientes
- ✉️ Recordatorios Enviados

**Botón Actualizar:** Recarga datos desde el backend

---

## 📱 RESPONSIVE

✅ Desktop: Botones en línea  
✅ Tablet: Botones apilados  
✅ Mobile: Diseño adaptativo completo  

---

## 🐛 DEBUG

### Ver Logs en Navegador

```javascript
// Abrir DevTools → Console
// Buscar:
"🚀 Enviando recordatorios..."
"✅ Respuesta:", { success: true, ... }
"❌ Error:", ...
```

### Ver GraphQL Request

```javascript
// En DevTools → Network → graphql
// Request Payload:
{
  "query": "mutation { forzarEnvioRecordatorios }"
}
```

---

## 📞 CONTACTO

**Si hay errores en la integración:**

1. ✅ Verificar que Angular compila sin errores
2. ⏳ Verificar que Spring Boot tiene IAResolver
3. ⏳ Verificar que FastAPI está corriendo
4. ⏳ Verificar que MongoDB tiene datos

**Todo el código fuente está en:**
- `src/app/services/ia.service.ts`
- `src/app/pages/ventas/venta-list/`

---

## 📚 DOCUMENTACIÓN COMPLETA

Ver: `IMPLEMENTACION-RECORDATORIOS-IA.md` (documento detallado)

---

**FIN DE GUÍA RÁPIDA**

*Versión: 1.0*  
*Fecha: 11 Nov 2025*
