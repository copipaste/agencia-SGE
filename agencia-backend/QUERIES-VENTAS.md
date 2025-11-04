# 💰 Queries y Mutations - Ventas

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Ventas** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todas las ventas
```graphql
query {
  getAllVentas {
    id
    clienteId
    cliente {
      id
      usuario {
        nombre
        apellido
        email
        telefono
      }
      direccion
    }
    agenteId
    agente {
      id
      usuario {
        nombre
        apellido
        email
      }
      puesto
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    detalles {
      id
      ventaId
      servicioId
      servicio {
        nombreServicio
        tipoServicio
        destinoCiudad
      }
      paqueteId
      paquete {
        nombrePaquete
        destinoPrincipal
      }
      cantidad
      precioUnitarioVenta
      subtotal
    }
  }
}
```
**Descripción**: Retorna la lista completa de todas las ventas registradas en el sistema con sus detalles.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener venta por ID
```graphql
query {
  getVentaById(id: "673cd79d2e9af5567b9c0c30") {
    id
    clienteId
    cliente {
      usuario {
        nombre
        apellido
        email
        telefono
      }
      direccion
      numeroPasaporte
    }
    agenteId
    agente {
      usuario {
        nombre
        apellido
        email
      }
      puesto
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    detalles {
      id
      servicioId
      servicio {
        nombreServicio
        tipoServicio
        destinoCiudad
        destinoPais
      }
      paqueteId
      paquete {
        nombrePaquete
        descripcion
        destinoPrincipal
        duracionDias
      }
      cantidad
      precioUnitarioVenta
      subtotal
    }
  }
}
```
**Descripción**: Retorna una venta específica basada en su ID, incluyendo todos los detalles de servicios y paquetes.

**Parámetros**:
- `id` (ID!): ID único de la venta (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener ventas por cliente
```graphql
query {
  getVentasByClienteId(clienteId: "673cd79d2e9af5567b9c0bf7") {
    id
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    agente {
      usuario {
        nombre
        apellido
      }
    }
    detalles {
      servicio {
        nombreServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      subtotal
    }
  }
}
```
**Descripción**: Retorna todas las ventas realizadas a un cliente específico.

**Parámetros**:
- `clienteId` (ID!): ID del cliente

**Permisos**: Requiere autenticación

---

### 4️⃣ Obtener ventas por agente
```graphql
query {
  getVentasByAgenteId(agenteId: "673cd79d2e9af5567b9c0bf8") {
    id
    cliente {
      usuario {
        nombre
        apellido
        email
      }
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    detalles {
      servicio {
        nombreServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      subtotal
    }
  }
}
```
**Descripción**: Retorna todas las ventas realizadas por un agente específico.

**Parámetros**:
- `agenteId` (ID!): ID del agente

**Permisos**: Requiere autenticación (Admin o el mismo Agente)

---

### 5️⃣ Obtener ventas por estado
```graphql
query {
  getVentasByEstado(estado: "Confirmada") {
    id
    cliente {
      usuario {
        nombre
        apellido
      }
    }
    agente {
      usuario {
        nombre
        apellido
      }
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
  }
}
```
**Descripción**: Retorna todas las ventas con un estado específico.

**Parámetros**:
- `estado` (String!): Estado de la venta (Pendiente, Confirmada, Pagada, Cancelada, Completada)

**Permisos**: Requiere autenticación (Admin o Agente)

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nueva venta
```graphql
mutation {
  createVenta(input: {
    clienteId: "673cd79d2e9af5567b9c0bf7"
    agenteId: "673cd79d2e9af5567b9c0bf8"
    estadoVenta: "Pendiente"
    metodoPago: "Tarjeta de Crédito"
    detalles: [
      {
        servicioId: "673cd79d2e9af5567b9c0c10"
        cantidad: 2
        precioUnitarioVenta: 450.00
      },
      {
        paqueteId: "673cd79d2e9af5567b9c0c20"
        cantidad: 1
        precioUnitarioVenta: 2800.00
      }
    ]
  }) {
    id
    cliente {
      usuario {
        nombre
        apellido
      }
    }
    agente {
      usuario {
        nombre
        apellido
      }
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    detalles {
      servicio {
        nombreServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      precioUnitarioVenta
      subtotal
    }
  }
}
```
**Descripción**: Crea una nueva venta en el sistema con sus detalles de servicios y/o paquetes.

**Input**:
- `clienteId` (ID!): ID del cliente que realiza la compra
- `agenteId` (ID!): ID del agente que realiza la venta
- `estadoVenta` (String!): Estado inicial de la venta
- `metodoPago` (String!): Método de pago utilizado
- `detalles` ([DetalleVentaItemInput!]!): Lista de items de la venta

**Detalle de Venta Input**:
- `servicioId` (ID): ID del servicio (mutuamente excluyente con paqueteId)
- `paqueteId` (ID): ID del paquete turístico (mutuamente excluyente con servicioId)
- `cantidad` (Int!): Cantidad de unidades
- `precioUnitarioVenta` (Float!): Precio unitario de venta

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: La venta creada con todos sus datos

**⚠️ Nota**: 
- Cada detalle debe tener servicioId O paqueteId, no ambos
- El `montoTotal` se calcula automáticamente: suma de todos los subtotales
- El `subtotal` de cada detalle se calcula: cantidad × precioUnitarioVenta
- La `fechaVenta` se establece automáticamente al momento de creación

---

### 2️⃣ Actualizar venta
```graphql
mutation {
  updateVenta(
    id: "673cd79d2e9af5567b9c0c30"
    input: {
      estadoVenta: "Pagada"
      metodoPago: "Transferencia Bancaria"
      detalles: [
        {
          servicioId: "673cd79d2e9af5567b9c0c10"
          cantidad: 3
          precioUnitarioVenta: 450.00
        },
        {
          paqueteId: "673cd79d2e9af5567b9c0c20"
          cantidad: 1
          precioUnitarioVenta: 2800.00
        }
      ]
    }
  ) {
    id
    estadoVenta
    metodoPago
    montoTotal
    detalles {
      servicio {
        nombreServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      subtotal
    }
  }
}
```
**Descripción**: Actualiza los datos de una venta existente.

**Parámetros**:
- `id` (ID!): ID de la venta a actualizar

**Input** (todos opcionales):
- `estadoVenta` (String): Nuevo estado de la venta
- `metodoPago` (String): Nuevo método de pago
- `detalles` ([DetalleVentaItemInput!]): Nueva lista completa de detalles (reemplaza la anterior)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: La venta actualizada

**⚠️ Nota**: Si se proporciona `detalles`, reemplaza completamente los detalles anteriores y recalcula el `montoTotal`.

---

### 3️⃣ Eliminar venta
```graphql
mutation {
  deleteVenta(id: "673cd79d2e9af5567b9c0c30")
}
```
**Descripción**: Elimina una venta del sistema.

**Parámetros**:
- `id` (ID!): ID de la venta a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Estados de Venta**: Los estados posibles son:
   - **Pendiente**: Venta creada, esperando confirmación
   - **Confirmada**: Venta confirmada por el cliente
   - **Pagada**: Pago recibido y verificado
   - **Completada**: Servicio prestado completamente
   - **Cancelada**: Venta cancelada

4. **Métodos de Pago**: Los métodos comunes son:
   - Efectivo
   - Tarjeta de Crédito
   - Tarjeta de Débito
   - Transferencia Bancaria
   - QR/Billetera Digital
   - Cheque

5. **Cálculo de Montos**:
   - `subtotal` = cantidad × precioUnitarioVenta (por cada detalle)
   - `montoTotal` = suma de todos los subtotales

6. **Detalles de Venta**:
   - Cada detalle representa un servicio o paquete vendido
   - Debe especificar servicioId O paqueteId, no ambos
   - Puede haber múltiples detalles en una venta

7. **Fecha de Venta**: Se establece automáticamente en formato ISO 8601 al crear la venta

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllVentas | ❌ | ✅ | ✅ |
| getVentaById | ❌* | ✅ | ✅ |
| getVentasByClienteId | ✅* | ✅ | ✅ |
| getVentasByAgenteId | ❌ | ✅* | ✅ |
| getVentasByEstado | ❌ | ✅ | ✅ |
| createVenta | ❌ | ✅ | ✅ |
| updateVenta | ❌ | ✅ | ✅ |
| deleteVenta | ❌ | ❌ | ✅ |

*Solo puede ver sus propias ventas

---

## 🧪 Ejemplos de Uso Completo

### Crear venta con servicios individuales
```graphql
mutation {
  createVenta(input: {
    clienteId: "673cd79d2e9af5567b9c0bf7"
    agenteId: "673cd79d2e9af5567b9c0bf8"
    estadoVenta: "Pendiente"
    metodoPago: "Efectivo"
    detalles: [
      {
        servicioId: "SERVICIO_HOTEL_ID"
        cantidad: 2
        precioUnitarioVenta: 450.00
      },
      {
        servicioId: "SERVICIO_VUELO_ID"
        cantidad: 2
        precioUnitarioVenta: 1100.00
      },
      {
        servicioId: "SERVICIO_TOUR_ID"
        cantidad: 2
        precioUnitarioVenta: 350.00
      }
    ]
  }) {
    id
    montoTotal
    fechaVenta
    detalles {
      servicio {
        nombreServicio
        tipoServicio
      }
      cantidad
      precioUnitarioVenta
      subtotal
    }
  }
}

# Resultado esperado:
# montoTotal: 3800.00 (900 + 2200 + 700)
```

### Crear venta con paquete turístico
```graphql
mutation {
  createVenta(input: {
    clienteId: "673cd79d2e9af5567b9c0bf7"
    agenteId: "673cd79d2e9af5567b9c0bf8"
    estadoVenta: "Pendiente"
    metodoPago: "Tarjeta de Crédito"
    detalles: [
      {
        paqueteId: "PAQUETE_UYUNI_ID"
        cantidad: 1
        precioUnitarioVenta: 2800.00
      }
    ]
  }) {
    id
    montoTotal
    fechaVenta
    detalles {
      paquete {
        nombrePaquete
        destinoPrincipal
        duracionDias
      }
      cantidad
      precioUnitarioVenta
      subtotal
    }
  }
}
```

### Crear venta mixta (servicios + paquetes)
```graphql
mutation {
  createVenta(input: {
    clienteId: "673cd79d2e9af5567b9c0bf7"
    agenteId: "673cd79d2e9af5567b9c0bf8"
    estadoVenta: "Confirmada"
    metodoPago: "Transferencia Bancaria"
    detalles: [
      {
        paqueteId: "PAQUETE_TITICACA_ID"
        cantidad: 2
        precioUnitarioVenta: 1500.00
      },
      {
        servicioId: "SERVICIO_SEGURO_ID"
        cantidad: 2
        precioUnitarioVenta: 50.00
      },
      {
        servicioId: "SERVICIO_CENA_ESPECIAL_ID"
        cantidad: 2
        precioUnitarioVenta: 150.00
      }
    ]
  }) {
    id
    montoTotal
    fechaVenta
    estadoVenta
    metodoPago
    detalles {
      servicio {
        nombreServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      subtotal
    }
  }
}

# Resultado esperado:
# montoTotal: 3400.00 (3000 + 100 + 300)
```

### Consultar ventas de un cliente
```graphql
query {
  getVentasByClienteId(clienteId: "673cd79d2e9af5567b9c0bf7") {
    id
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
    agente {
      usuario {
        nombre
        apellido
      }
    }
    detalles {
      servicio {
        nombreServicio
        tipoServicio
      }
      paquete {
        nombrePaquete
      }
      cantidad
      subtotal
    }
  }
}
```

### Consultar ventas de un agente (para reportes)
```graphql
query {
  getVentasByAgenteId(agenteId: "673cd79d2e9af5567b9c0bf8") {
    id
    cliente {
      usuario {
        nombre
        apellido
        email
      }
    }
    fechaVenta
    montoTotal
    estadoVenta
    metodoPago
  }
}
```

### Actualizar estado de venta (flujo completo)
```graphql
# 1. Venta creada como Pendiente
mutation {
  createVenta(input: {
    clienteId: "CLIENT_ID"
    agenteId: "AGENT_ID"
    estadoVenta: "Pendiente"
    metodoPago: "Tarjeta de Crédito"
    detalles: [...]
  }) {
    id
    estadoVenta
  }
}

# 2. Cliente confirma la compra
mutation {
  updateVenta(
    id: "VENTA_ID"
    input: {
      estadoVenta: "Confirmada"
    }
  ) {
    id
    estadoVenta
  }
}

# 3. Se recibe el pago
mutation {
  updateVenta(
    id: "VENTA_ID"
    input: {
      estadoVenta: "Pagada"
    }
  ) {
    id
    estadoVenta
  }
}

# 4. Servicio completado
mutation {
  updateVenta(
    id: "VENTA_ID"
    input: {
      estadoVenta: "Completada"
    }
  ) {
    id
    estadoVenta
  }
}
```

### Consultar ventas por estado (para reportes)
```graphql
# Ventas pendientes
query {
  getVentasByEstado(estado: "Pendiente") {
    id
    cliente {
      usuario {
        nombre
        apellido
        email
      }
    }
    fechaVenta
    montoTotal
  }
}

# Ventas completadas del mes
query {
  getVentasByEstado(estado: "Completada") {
    id
    cliente {
      usuario {
        nombre
        apellido
      }
    }
    agente {
      usuario {
        nombre
        apellido
      }
    }
    fechaVenta
    montoTotal
  }
}
```

---

## 📊 Flujo de Estados de Venta

```
┌──────────┐
│ Pendiente │ ← Venta creada
└─────┬────┘
      │
      ↓
┌────────────┐
│ Confirmada │ ← Cliente confirma
└─────┬──────┘
      │
      ↓
┌──────┐
│ Pagada│ ← Pago recibido
└───┬──┘
    │
    ↓
┌───────────┐
│ Completada│ ← Servicio prestado
└───────────┘

Cualquier estado → Cancelada (si se cancela)
```

---

## 💡 Buenas Prácticas

1. **Precios consistentes**: Use los precios actuales al momento de la venta
2. **Documentación completa**: Incluya todos los detalles necesarios
3. **Estados claros**: Actualice el estado según el progreso de la venta
4. **Trazabilidad**: Registre cliente y agente en cada venta
5. **Validación de inventario**: Verifique disponibilidad antes de crear venta
6. **Método de pago**: Registre correctamente el método de pago usado
7. **Seguimiento**: Use queries por estado para hacer seguimiento

---

## 📈 Consultas para Reportes

### Ventas totales
```graphql
query {
  getAllVentas {
    id
    fechaVenta
    montoTotal
    estadoVenta
  }
}
```

### Ventas de un período (filtrar en frontend)
```graphql
query {
  getAllVentas {
    id
    fechaVenta
    montoTotal
    cliente {
      usuario {
        nombre
        apellido
      }
    }
    agente {
      usuario {
        nombre
        apellido
      }
    }
  }
}
```

### Rendimiento de agentes
```graphql
query {
  getVentasByAgenteId(agenteId: "AGENTE_ID") {
    id
    fechaVenta
    montoTotal
    estadoVenta
  }
}
```
