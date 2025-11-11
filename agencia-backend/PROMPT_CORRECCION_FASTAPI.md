# 🚨 PROMPT PARA FASTAPI - CORRECCIÓN URGENTE

Copia y pega este prompt completo en el chat de FastAPI:

---

# 🚨 PROBLEMA CRÍTICO DETECTADO

Spring Boot está enviando **TODOS los campos correctamente**, pero FastAPI sigue detectándolo como `PredictRequest` (básico).

## 📊 EVIDENCIA

### Logs de Spring Boot (CORRECTO):
```
✅ ventaId: 69133dea97fc4685fa3ef7a7
✅ clienteId: 690f40b67c5da533458cd875
✅ emailCliente: alan@gmail.com
✅ nombreCliente: Alan Romero
✅ nombrePaquete: tour oruro
✅ destino: oruro
✅ fechaVenta: 2025-11-29T00:00
✅ montoTotal: 600.0
✅ Features ML: es_temporada_alta=0, metodo_pago_tarjeta=0, tiene_paquete=1
```

**TODOS LOS CAMPOS ESTÁN PRESENTES** ✅

### Logs de FastAPI (INCORRECTO):
```
❌ Request tipo: PredictRequest (básico) - No se guarda en MongoDB
```

### Error en Spring Boot:
```
Cannot invoke "java.lang.Double.doubleValue()" because the return value of 
"PredictResponseDTO.getProbabilidadCancelacion()" is null
```

**Esto indica que FastAPI está retornando un response incompleto.**

---

## 🔍 DIAGNÓSTICO

El problema está en el **endpoint POST /predict** de FastAPI.

**Probablemente el código tiene:**

```python
@app.post("/predict")
async def predict(request: Union[PredictRequest, PredictRequestFull]):
    # Pydantic está eligiendo PredictRequest en lugar de PredictRequestFull
    ...
```

**Problema:** Pydantic está haciendo match con `PredictRequest` primero porque todos los campos de `PredictRequest` están presentes.

---

## ✅ SOLUCIÓN REQUERIDA EN FASTAPI

### Opción 1: Usar Solo PredictRequestFull (RECOMENDADO)

```python
@app.post("/predict")
async def predict(request: PredictRequestFull):
    # Ahora SIEMPRE espera el request completo
    
    # Hacer predicción
    prediccion = predictor.predecir(request)
    
    # Si probabilidad >= 0.70, guardar en MongoDB
    if prediccion["probabilidad_cancelacion"] >= 0.70:
        # Guardar en MongoDB
        db.predicciones_cancelacion.insert_one({
            "venta_id": request.venta_id,
            "cliente_id": request.cliente_id,
            "email_cliente": request.email_cliente,
            "nombre_cliente": request.nombre_cliente,
            "nombre_paquete": request.nombre_paquete,
            "destino": request.destino,
            "fecha_venta": request.fecha_venta,
            "monto_total": request.monto_total,
            "probabilidad_cancelacion": prediccion["probabilidad_cancelacion"],
            "recomendacion": prediccion["recomendacion"],
            "factores_riesgo": prediccion["factores_riesgo"],
            "recordatorio_enviado": False,
            "created_at": datetime.utcnow()
        })
    
    return prediccion
```

---

## 🎯 CAMBIOS ESPECÍFICOS NECESARIOS

### 1. Archivo: `app/schemas.py`

Verificar que `PredictRequestFull` tenga **TODOS** estos campos marcados como requeridos:

```python
class PredictRequestFull(BaseModel):
    # IDs
    venta_id: str
    cliente_id: str
    
    # Datos adicionales (DEBEN SER REQUERIDOS)
    email_cliente: str                    # ← NO debe ser Optional
    nombre_cliente: str                   # ← NO debe ser Optional
    nombre_paquete: Optional[str] = None  # ← Este SÍ puede ser opcional
    destino: Optional[str] = None         # ← Este SÍ puede ser opcional
    fecha_venta: str                      # ← NO debe ser Optional
    
    # Features ML (11)
    monto_total: float
    es_temporada_alta: int
    dia_semana_reserva: int
    metodo_pago_tarjeta: int
    tiene_paquete: int
    duracion_dias: int
    destino_categoria: int
    total_compras_previas: int
    total_cancelaciones_previas: int
    tasa_cancelacion_historica: float
    monto_promedio_compras: float
```

### 2. Archivo: `app/routers/prediccion.py` (o donde esté el endpoint)

```python
@router.post("/predict")
async def predict(request: PredictRequestFull):  # ← Usar SOLO PredictRequestFull
    logger.info(f"📊 Predicción solicitada para venta: {request.venta_id}")
    
    # Hacer predicción
    resultado = predictor.predecir(request)
    
    logger.info(f"✅ Predicción exitosa: {resultado['probabilidad_cancelacion']*100:.2f}% - {resultado['recomendacion']}")
    
    # ✅ AGREGAR ESTE CÓDIGO
    if resultado["probabilidad_cancelacion"] >= 0.70:
        logger.info(f"📝 Request tipo: PredictRequestFull - Intentando guardar en MongoDB...")
        
        try:
            doc = {
                "venta_id": request.venta_id,
                "cliente_id": request.cliente_id,
                "email_cliente": request.email_cliente,
                "nombre_cliente": request.nombre_cliente,
                "nombre_paquete": request.nombre_paquete,
                "destino": request.destino,
                "fecha_venta": request.fecha_venta,
                "monto_total": request.monto_total,
                "probabilidad_cancelacion": resultado["probabilidad_cancelacion"],
                "recomendacion": resultado["recomendacion"],
                "factores_riesgo": resultado.get("factores_riesgo", []),
                "recordatorio_enviado": False,
                "fecha_prediccion": datetime.utcnow(),
                "created_at": datetime.utcnow()
            }
            
            result = db.predicciones_cancelacion.insert_one(doc)
            logger.warning(f"🚨 ✅ ALERTA GUARDADA EXITOSAMENTE: {request.venta_id} - {resultado['probabilidad_cancelacion']*100:.0f}% riesgo")
            
        except Exception as e:
            logger.error(f"❌ Error guardando en MongoDB: {e}")
    else:
        logger.info(f"📝 Probabilidad {resultado['probabilidad_cancelacion']*100:.2f}% < 70% - No se guarda")
    
    return {
        "success": True,
        "venta_id": request.venta_id,
        "cliente_id": request.cliente_id,
        "probabilidad_cancelacion": resultado["probabilidad_cancelacion"],
        "recomendacion": resultado["recomendacion"],
        "factores_riesgo": resultado.get("factores_riesgo", [])
    }
```

---

## 🧪 TESTING DESPUÉS DE LA CORRECCIÓN

### Debe mostrar en FastAPI:

```
2025-11-11 09:50:00 | INFO     | 📊 Predicción solicitada para venta: 69133dea97fc4685fa3ef7a7
2025-11-11 09:50:00 | INFO     | ✅ Predicción exitosa: 86.11% - enviar_recordatorio
2025-11-11 09:50:00 | INFO     | 📝 Request tipo: PredictRequestFull - Intentando guardar en MongoDB...
2025-11-11 09:50:00 | WARNING  | 🚨 ✅ ALERTA GUARDADA EXITOSAMENTE: 69133dea97fc4685fa3ef7a7 - 86% riesgo
INFO:     127.0.0.1:63053 - "POST /predict HTTP/1.1" 200 OK
```

---

## 📋 CHECKLIST DE CORRECCIÓN

- [ ] Eliminar `Union[PredictRequest, PredictRequestFull]` del endpoint
- [ ] Usar solo `PredictRequestFull` en el endpoint
- [ ] Verificar que `email_cliente`, `nombre_cliente`, `fecha_venta` son requeridos en el schema
- [ ] Agregar código de guardado en MongoDB cuando probabilidad >= 0.70
- [ ] Agregar logs detallados del guardado
- [ ] Retornar response completo con `probabilidad_cancelacion`

---

## 🎯 RESULTADO ESPERADO

Después de esta corrección:
1. ✅ FastAPI reconocerá el request como `PredictRequestFull`
2. ✅ Guardará en MongoDB si probabilidad >= 70%
3. ✅ Spring Boot recibirá response completo sin errores
4. ✅ La colección `predicciones_cancelacion` aparecerá en MongoDB

---

**Por favor, aplica estas correcciones, reinicia FastAPI y prueba nuevamente.**

