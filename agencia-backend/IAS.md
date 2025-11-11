Perfecto, vamos a dejar esto “redondito” para dos públicos:

1. **Para Flutter**: qué son las 3 IA, para qué sirven, qué endpoint van a llamar, qué datos tendrán que mandar y qué esperar como respuesta (aunque todavía no exista el modelo).
2. **Para Spring/IntelliJ**: un prompt para que el backend deje creadas las capas (controllers, DTOs, services) y los endpoints REST “stub” que luego se van a conectar al modelo real.

Te lo dejo en dos secciones separadas en el mismo `.md` para que puedas copiarlo tal cual.

---

````markdown
# 🤖 Diseño de las 3 IA del Proyecto – Agencia de Viajes

**Objetivo del documento:**  
Explicar qué hace cada una de las 3 IA exigidas por el proyecto, dónde vivirá (microservicio / externo), qué datos espera, qué responde y cómo la app móvil Flutter la va a consumir.  
También se incluye un prompt para el backend Spring Boot (IntelliJ) para dejar listos los endpoints.

---

## 🧠 Visión General

El sistema tendrá **3 capacidades de IA**:

1. **IA 1 – Predicción de Cancelación (supervisado)**  
   - Tipo: modelo entrenado por nosotros  
   - Lenguaje: Python (FastAPI o Flask)  
   - Uso: decir qué reservas tienen alta probabilidad de ser canceladas → mandar recordatorios

2. **IA 2 – Segmentación de Clientes (no supervisado)**  
   - Tipo: clustering (K-Means) entrenado por nosotros  
   - Lenguaje: Python  
   - Uso: dar recomendaciones/promociones según el tipo de cliente

3. **IA 3 – Recomendador Multimodal (texto + imagen)**  
   - Tipo: NO lo entrenamos nosotros. Usamos una API existente (ChatGPT Vision, Gemini, o similar)  
   - Uso: el cliente manda una foto o descripción de destino y el sistema responde “estos paquetes de nuestra base se parecen”

Las 3 IA **no están entrenadas todavía**, pero la app móvil y el backend deben quedar listos para conectarse.

---

## 1. IA 1 – Predicción de Cancelación

### ¿Qué hace?
Recibe datos de **una reserva/venta** (cliente, destino, fecha, estado, monto, anticipación, etc.) y devuelve un número entre 0 y 1 que indica la **probabilidad de que esa reserva sea cancelada**.

### ¿Por qué existe?
Porque en tu modelo de datos solo tienes 3 estados (`Pendiente`, `Confirmada`, `Cancelada`). La IA sirve para decir: “esta que está `Pendiente` tiene pinta de cancelarse → mándale push”.

### ¿Dónde se implementará?
- Microservicio aparte en **Python**.
- Ese microservicio tendrá un endpoint tipo:  
  `POST http://ia-cancelacion/api/predict`
- El backend de **Spring** actuará como puente: Flutter le llama a Spring y Spring llama al microservicio IA.

### Datos de entrada (ejemplo)
```json
{
  "ventaId": "venta001",
  "clienteId": "cli001",
  "destino": "Cancún",
  "fechaInicio": "2025-12-15",
  "diasAnticipacion": 30,
  "montoTotal": 1850.0,
  "metodoPago": "APP_MOBILE",
  "estadoVenta": "Pendiente",
  "frecuenciaCliente": 3,
  "ultimaCompraHaceDias": 90
}
````

### Datos de salida (ejemplo)

```json
{
  "ventaId": "venta001",
  "probCancelacion": 0.82,
  "recomendacion": "enviar_recordatorio"
}
```

### ¿Cómo lo usará Flutter?

* Flutter **no necesita** mandar todos esos datos.
* Flutter puede llamar a tu backend:
  `POST /api/ia/cancelacion/predict` con solo `ventaId`
* Spring busca la venta en Mongo, arma el JSON completo y se lo manda al microservicio de IA.
* El resultado lo devuelve a Flutter para que:

  * lo muestre, o
  * lo guarde localmente (para notificaciones), o
  * simplemente ignore si no lo necesita.

### Caso de uso en la app

* El cliente crea una reserva.
* La app o el backend llaman a IA 1.
* Si la probabilidad es alta → la app (o n8n/Firebase) envía notificación “confirma tu viaje”.
* Esto también se puede ejecutar desde n8n de forma programada.

---

## 2. IA 2 – Segmentación de Clientes (No Supervisado)

### ¿Qué hace?

Agarra el historial del cliente (cuánto gasta, a dónde viaja, cada cuánto compra) y lo mete en un modelo de clustering (K-Means).
El modelo responde: **“este cliente es del grupo 2 – playero, gasto medio, compra poco frecuente”**.

### ¿Por qué existe?

Para que la app pueda mostrar **“Paquetes recomendados para ti”** en el Home.
La lógica será: si eres del cluster 2 → muestra paquetes de playa; si eres del cluster 4 → muestra paquetes de aventura, etc.

### ¿Dónde se implementará?

* También como microservicio en **Python** (puede compartir BD Postgres con el módulo BI).
* Endpoint típico:
  `GET http://ia-segmentacion/api/recommendations?clienteId=...`
* Spring tendrá un endpoint espejo:
  `GET /api/ia/recomendaciones`
  que llama al microservicio y formatea los paquetes.

### Datos de entrada (IA Python)

```json
{
  "clienteId": "cli001",
  "totalCompras": 5,
  "montoPromedio": 900.0,
  "destinosFrecuentes": ["Playa", "Caribe"],
  "ultimaCompraHaceDias": 45
}
```

### Datos de salida (IA Python)

```json
{
  "clienteId": "cli001",
  "cluster": 2,
  "paquetesRecomendados": [
    { "id": "paq001", "score": 0.93 },
    { "id": "paq004", "score": 0.75 }
  ]
}
```

### ¿Cómo lo usará Flutter?

* Flutter llama a `GET /api/ia/recomendaciones` (sin params, el backend ya sabe quién es por el token).
* El backend llama al microservicio de IA.
* El backend después hace un `findById` en Mongo para cada `paqueteId` y devuelve a Flutter objetos completos, listos para renderizar.

**Respuesta que verá Flutter:**

```json
{
  "success": true,
  "message": "Recomendaciones generadas",
  "data": [
    {
      "id": "paq001",
      "nombrePaquete": "Caribe Paradisíaco",
      "destinoPrincipal": "Cancún",
      "precioTotalVenta": 1850.0,
      "duracionDias": 7
    },
    {
      "id": "paq004",
      "nombrePaquete": "Aventura en Playa",
      "destinoPrincipal": "Punta Cana",
      "precioTotalVenta": 1200.0,
      "duracionDias": 5
    }
  ]
}
```

---

## 3. IA 3 – Recomendador Multimodal (Texto + Imagen)

### ¿Qué hace?

El cliente manda una **descripción y/o una imagen** (“quiero ir a una playa tranquila con familia”, o una foto de una playa).
La IA compara eso contra tu catálogo de paquetes y devuelve los más parecidos.

### Diferencia con las otras 2

* Esta NO la entrenas tú.
* Usas un proveedor externo (p. ej. **OpenAI GPT-4o** con visión, **Gemini** con imagen, **Claude** si soporta imagen).
* Tu backend solo envía:

  1. el prompt del usuario (texto),
  2. la URL/base64 de la imagen,
  3. y **tu catálogo** (texto resumido de tus paquetes),
     y el modelo responde cuál es más parecido.

### ¿Dónde se implementará?

Tienes 2 opciones válidas:

1. **Opción A (recomendada):** Flutter → Spring → API externa

  * Flutter manda texto/imagen a `/api/ia/asistente-paquetes`
  * Spring mete la API key y llama al modelo externo
  * Spring filtra/da formato y devuelve a Flutter
    ✅ ventaja: no expones la API KEY en Flutter

2. **Opción B:** Flutter → API externa directamente

  * Solo si alguna vez quieres testear rápido
  * ❌ pero aquí la key queda en el cliente

Te conviene la **Opción A**.

### Datos de entrada (Flutter → Spring)

```json
{
  "descripcion": "playa tranquila para ir en familia en diciembre",
  "imagenBase64": "<opcional>",
  "presupuestoMax": 2000
}
```

### Datos de salida (Spring → Flutter)

```json
{
  "success": true,
  "message": "Paquetes sugeridos según tu descripción",
  "data": [
    {
      "id": "paq001",
      "nombrePaquete": "Caribe Paradisíaco",
      "destinoPrincipal": "Cancún",
      "precioTotalVenta": 1850.0,
      "match": 0.91
    },
    {
      "id": "paq005",
      "nombrePaquete": "Playa Familiar Dominicana",
      "destinoPrincipal": "Punta Cana",
      "precioTotalVenta": 1700.0,
      "match": 0.85
    }
  ]
}
```

---

## 🚀 Prompt para IntelliJ / Spring Boot

Este prompt es para que el backend quede **preparado** (controladores + DTOs + servicios vacíos) sin que todavía exista el modelo de IA en Python o la API externa.

### Prompt

> **Contexto:**
> Proyecto Spring Boot 3 con MongoDB. Ya existe autenticación JWT y los endpoints REST para la app móvil (`/api/auth`, `/api/paquetes`, `/api/ventas`, `/api/clientes`).
> Ahora quiero preparar la integración con 3 servicios de IA externos (2 propios en Python y 1 externo tipo OpenAI/Gemini).
>
> **Tareas:**
>
> 1. Crear un controller REST `IAController` en el paquete `com.agencia.agencia_backend.controller` con base path `/api/ia`.
> 2. Dentro del controller, crear 3 endpoints:
     >
     >    * `POST /api/ia/cancelacion/predict`
            >
            >      * RequestBody: `PredictCancelacionRequest` (campos: `ventaId`, opcional `clienteId`)
>      * Response: `ApiResponse<PredictCancelacionResponse>`
>      * Por ahora que devuelva un `501 Not Implemented` con mensaje claro.
>    * `GET /api/ia/recomendaciones`
       >
       >      * Usa el usuario autenticado para obtener el `clienteId`
>      * Response: `ApiResponse<List<PaqueteTuristicoDTO>>`
>      * Por ahora devolver lista vacía y 501.
>    * `POST /api/ia/asistente-paquetes`
       >
       >      * RequestBody: `AsistentePaquetesRequest` (campos: `descripcion`, `imagenBase64` opcional, `presupuestoMax` opcional)
>      * Response: `ApiResponse<List<PaqueteTuristicoDTO>>`
>      * Por ahora devolver 501.
> 3. Crear los DTOs en el paquete `com.agencia.agencia_backend.dto.ia`:
     >
     >    * `PredictCancelacionRequest`
>    * `PredictCancelacionResponse` (campos: `ventaId`, `probCancelacion`, `recomendacion`)
>    * `AsistentePaquetesRequest`
> 4. Crear un servicio `IAIntegrationService` en `com.agencia.agencia_backend.service` con métodos:
     >
     >    * `predictCancelacion(String ventaId, String clienteId)`
>    * `getRecomendacionesParaCliente(String clienteId)`
>    * `buscarPaquetesPorDescripcion(AsistentePaquetesRequest request)`
       >      Por ahora que los 3 métodos lancen `UnsupportedOperationException("IA no implementada aún")`.
> 5. Asegurarse de que los endpoints `/api/ia/**` estén protegidos por JWT (solo clientes) en `SecurityConfig`.
> 6. Mantener el formato de respuesta estándar `ApiResponse<T>` que ya tiene el proyecto.
>
> **Objetivo:** dejar lista la estructura para que, cuando el microservicio de IA en Python esté disponible, solo tenga que implementarse la llamada HTTP desde `IAIntegrationService`.

---

## 📲 Resumen para Flutter

* **IA 1**: Flutter llama a `/api/ia/cancelacion/predict` pasando solo `ventaId`. Lo usará para mostrar o disparar recordatorios.
* **IA 2**: Flutter llama a `/api/ia/recomendaciones` al cargar el Home. Si el backend aún devuelve 501, Flutter solo oculta la sección.
* **IA 3**: Flutter envía descripción/imagen a `/api/ia/asistente-paquetes` y muestra los paquetes que devuelva. Si el backend aún devuelve 501, mostrar mensaje “asistente no disponible”.

De esta forma Flutter **ya puede dejar la pantalla y los servicios escritos** sin que la IA exista todavía.

---

```

Con esto ya tienes: explicación funcional (para Flutter), forma de integración (para no exponer API key) y el prompt para IntelliJ para que el backend quede “IA-ready”.  

Cuando quieras pasamos al siguiente paso: **planificar el dataset y las features de la IA 1 (predicción de cancelación)**.
```
