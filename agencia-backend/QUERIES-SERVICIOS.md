# 🎫 Queries y Mutations - Servicios

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Servicios** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todos los servicios
```graphql
query {
  getAllServicios {
    id
    proveedorId
    proveedor {
      id
      nombreEmpresa
      tipoServicio
      contactoEmail
    }
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioCosto
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Retorna la lista completa de todos los servicios registrados en el sistema.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener servicio por ID
```graphql
query {
  getServicioById(id: "673cd79d2e9af5567b9c0c10") {
    id
    proveedorId
    proveedor {
      nombreEmpresa
      tipoServicio
      contactoEmail
    }
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioCosto
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Retorna un servicio específico basado en su ID.

**Parámetros**:
- `id` (ID!): ID único del servicio (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener servicios por proveedor
```graphql
query {
  getServiciosByProveedorId(proveedorId: "673cd79d2e9af5567b9c0c01") {
    id
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    precioCosto
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Retorna todos los servicios ofrecidos por un proveedor específico.

**Parámetros**:
- `proveedorId` (ID!): ID del proveedor

**Permisos**: Requiere autenticación

---

### 4️⃣ Obtener servicios por tipo
```graphql
query {
  getServiciosByTipo(tipoServicio: "Hotel") {
    id
    proveedorId
    proveedor {
      nombreEmpresa
    }
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Retorna todos los servicios de un tipo específico.

**Parámetros**:
- `tipoServicio` (String!): Tipo de servicio (Hotel, Vuelo, Transporte, etc.)

**Permisos**: Requiere autenticación

---

### 5️⃣ Obtener servicios por destino (ciudad)
```graphql
query {
  getServiciosByDestino(destinoCiudad: "La Paz") {
    id
    proveedorId
    proveedor {
      nombreEmpresa
    }
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Retorna todos los servicios con destino a una ciudad específica.

**Parámetros**:
- `destinoCiudad` (String!): Ciudad de destino

**Permisos**: Requiere autenticación

---

### 6️⃣ Buscar servicios
```graphql
query {
  searchServicios(searchTerm: "Copacabana") {
    id
    proveedorId
    proveedor {
      nombreEmpresa
    }
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Busca servicios por nombre, descripción o destino.

**Parámetros**:
- `searchTerm` (String!): Término a buscar

**Permisos**: Requiere autenticación

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nuevo servicio
```graphql
mutation {
  createServicio(input: {
    proveedorId: "673cd79d2e9af5567b9c0c01"
    tipoServicio: "Hotel"
    nombreServicio: "Habitación Doble Estándar - Hotel Plaza"
    descripcion: "Habitación doble con desayuno incluido, WiFi, TV cable"
    destinoCiudad: "La Paz"
    destinoPais: "Bolivia"
    precioCosto: 300.00
    precioVenta: 450.00
    isAvailable: true
  }) {
    id
    proveedorId
    proveedor {
      nombreEmpresa
    }
    tipoServicio
    nombreServicio
    descripcion
    destinoCiudad
    destinoPais
    precioCosto
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Crea un nuevo servicio en el sistema.

**Input**:
- `proveedorId` (ID): ID del proveedor que ofrece el servicio (opcional)
- `tipoServicio` (String!): Tipo de servicio
- `nombreServicio` (String!): Nombre descriptivo del servicio
- `descripcion` (String): Descripción detallada (opcional)
- `destinoCiudad` (String): Ciudad de destino (opcional)
- `destinoPais` (String): País de destino (opcional)
- `precioCosto` (Float): Precio de costo del servicio (opcional)
- `precioVenta` (Float): Precio de venta al público (opcional)
- `isAvailable` (Boolean): Indica si el servicio está disponible (opcional, default: true)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El servicio creado con todos sus datos

---

### 2️⃣ Actualizar servicio
```graphql
mutation {
  updateServicio(
    id: "673cd79d2e9af5567b9c0c10"
    input: {
      nombreServicio: "Habitación Doble Premium - Hotel Plaza"
      descripcion: "Habitación doble premium con desayuno buffet, WiFi, TV cable, minibar"
      precioCosto: 350.00
      precioVenta: 500.00
      isAvailable: true
    }
  ) {
    id
    nombreServicio
    descripcion
    precioCosto
    precioVenta
    isAvailable
  }
}
```
**Descripción**: Actualiza los datos de un servicio existente.

**Parámetros**:
- `id` (ID!): ID del servicio a actualizar

**Input** (todos opcionales):
- `proveedorId` (ID): Nuevo proveedor
- `tipoServicio` (String): Nuevo tipo de servicio
- `nombreServicio` (String): Nuevo nombre
- `descripcion` (String): Nueva descripción
- `destinoCiudad` (String): Nueva ciudad destino
- `destinoPais` (String): Nuevo país destino
- `precioCosto` (Float): Nuevo precio de costo
- `precioVenta` (Float): Nuevo precio de venta
- `isAvailable` (Boolean): Nuevo estado de disponibilidad

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El servicio actualizado

---

### 3️⃣ Eliminar servicio
```graphql
mutation {
  deleteServicio(id: "673cd79d2e9af5567b9c0c10")
}
```
**Descripción**: Elimina un servicio del sistema.

**Parámetros**:
- `id` (ID!): ID del servicio a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

**⚠️ Nota**: Esta operación puede fallar si el servicio está incluido en paquetes turísticos activos.

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Tipos de Servicio**: Los tipos de servicio deben coincidir con los tipos de los proveedores:
   - Hotel
   - Vuelo
   - Transporte
   - Restaurante
   - Tour/Excursión
   - Crucero
   - Seguro

4. **Precios**: 
   - `precioCosto`: Precio que paga la agencia al proveedor
   - `precioVenta`: Precio que cobra la agencia al cliente
   - Margen = precioVenta - precioCosto

5. **Estado de Disponibilidad** (`isAvailable`): 
   - Campo booleano que indica si el servicio está disponible para venta
   - `true`: Servicio disponible (✅)
   - `false`: Servicio no disponible (❌)
   - Por defecto es `true` al crear un servicio

6. **Relaciones**:
   - Un servicio pertenece a un proveedor (opcional)
   - Un servicio puede estar en múltiples paquetes turísticos

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllServicios | ❌ | ✅ | ✅ |
| getServicioById | ❌ | ✅ | ✅ |
| getServiciosByProveedorId | ❌ | ✅ | ✅ |
| getServiciosByTipo | ❌ | ✅ | ✅ |
| getServiciosByDestino | ❌ | ✅ | ✅ |
| searchServicios | ❌ | ✅ | ✅ |
| createServicio | ❌ | ✅ | ✅ |
| updateServicio | ❌ | ✅ | ✅ |
| deleteServicio | ❌ | ❌ | ✅ |

---

## 🧪 Ejemplos de Uso Completo

### Crear servicio de hotel
```graphql
mutation {
  createServicio(input: {
    proveedorId: "673cd79d2e9af5567b9c0c01"
    tipoServicio: "Hotel"
    nombreServicio: "Suite Junior - Radisson Cochabamba"
    descripcion: "Suite junior con sala de estar, desayuno buffet, WiFi, gimnasio, piscina"
    destinoCiudad: "Cochabamba"
    destinoPais: "Bolivia"
    precioCosto: 500.00
    precioVenta: 750.00
    isAvailable: true
  }) {
    id
    nombreServicio
    precioVenta
    isAvailable
  }
}
```

### Crear servicio de vuelo
```graphql
mutation {
  createServicio(input: {
    proveedorId: "673cd79d2e9af5567b9c0c02"
    tipoServicio: "Vuelo"
    nombreServicio: "Vuelo La Paz - Santa Cruz (Ida y Vuelta)"
    descripcion: "Vuelo redondo La Paz - Santa Cruz, incluye equipaje de mano"
    destinoCiudad: "Santa Cruz"
    destinoPais: "Bolivia"
    precioCosto: 800.00
    precioVenta: 1100.00
    isAvailable: true
  }) {
    id
    nombreServicio
    precioVenta
    isAvailable
  }
}
```

### Buscar servicios de hotel en La Paz
```graphql
# Opción 1: Por tipo y destino
query {
  getServiciosByTipo(tipoServicio: "Hotel") {
    id
    nombreServicio
    destinoCiudad
    precioVenta
    isAvailable
  }
}

query {
  getServiciosByDestino(destinoCiudad: "La Paz") {
    id
    tipoServicio
    nombreServicio
    precioVenta
    isAvailable
  }
}

# Opción 2: Búsqueda general
query {
  searchServicios(searchTerm: "La Paz Hotel") {
    id
    nombreServicio
    descripcion
    destinoCiudad
    precioVenta
    isAvailable
  }
}
```

### Actualizar precios y estado de disponibilidad
```graphql
mutation {
  updateServicio(
    id: "673cd79d2e9af5567b9c0c10"
    input: {
      precioCosto: 400.00
      precioVenta: 600.00
      isAvailable: true
    }
  ) {
    id
    nombreServicio
    precioCosto
    precioVenta
    isAvailable
  }
}
```

### Consultar servicios de un proveedor específico
```graphql
query {
  getServiciosByProveedorId(proveedorId: "673cd79d2e9af5567b9c0c01") {
    id
    tipoServicio
    nombreServicio
    descripcion
    precioCosto
    precioVenta
    isAvailable
    proveedor {
      nombreEmpresa
      contactoEmail
      contactoTelefono
    }
  }
}
```

---

## 📊 Ejemplos de Servicios por Tipo

### 🏨 Hoteles
```graphql
mutation {
  createServicio(input: {
    proveedorId: "PROVEEDOR_ID"
    tipoServicio: "Hotel"
    nombreServicio: "Habitación Triple - Vista al Lago"
    descripcion: "Habitación triple con vista al lago Titicaca, desayuno incluido"
    destinoCiudad: "Copacabana"
    destinoPais: "Bolivia"
    precioCosto: 250.00
    precioVenta: 380.00
    isAvailable: true
  }) { id }
}
```

### ✈️ Vuelos
```graphql
mutation {
  createServicio(input: {
    proveedorId: "PROVEEDOR_ID"
    tipoServicio: "Vuelo"
    nombreServicio: "Vuelo La Paz - Cusco (Ida)"
    descripcion: "Vuelo directo, duración 1.5 horas, equipaje incluido"
    destinoCiudad: "Cusco"
    destinoPais: "Perú"
    precioCosto: 1200.00
    precioVenta: 1600.00
    isAvailable: true
  }) { id }
}
```

### 🚌 Transporte
```graphql
mutation {
  createServicio(input: {
    proveedorId: "PROVEEDOR_ID"
    tipoServicio: "Transporte"
    nombreServicio: "Bus Turístico La Paz - Uyuni"
    descripcion: "Bus cama, incluye almuerzo y paradas turísticas"
    destinoCiudad: "Uyuni"
    destinoPais: "Bolivia"
    precioCosto: 150.00
    precioVenta: 220.00
    isAvailable: true
  }) { id }
}
```

### 🎭 Tours
```graphql
mutation {
  createServicio(input: {
    proveedorId: "PROVEEDOR_ID"
    tipoServicio: "Tour"
    nombreServicio: "Tour Salar de Uyuni 3 días/2 noches"
    descripcion: "Tour completo al salar, incluye transporte, hospedaje, comidas y guía"
    destinoCiudad: "Uyuni"
    destinoPais: "Bolivia"
    precioCosto: 800.00
    precioVenta: 1200.00
    isAvailable: true
  }) { id }
}
```

### 🍽️ Restaurante
```graphql
mutation {
  createServicio(input: {
    proveedorId: "PROVEEDOR_ID"
    tipoServicio: "Restaurante"
    nombreServicio: "Cena Especial en Gustu"
    descripcion: "Menú degustación de 5 tiempos, bebidas incluidas"
    destinoCiudad: "La Paz"
    destinoPais: "Bolivia"
    precioCosto: 250.00
    precioVenta: 350.00
    isAvailable: true
  }) { id }
}
```
