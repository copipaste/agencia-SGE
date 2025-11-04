# 🏢 Queries y Mutations - Proveedores

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Proveedores** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todos los proveedores
```graphql
query {
  getAllProveedores {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Retorna la lista completa de todos los proveedores registrados en el sistema.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener proveedor por ID
```graphql
query {
  getProveedorById(id: "673cd79d2e9af5567b9c0c01") {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Retorna un proveedor específico basado en su ID.

**Parámetros**:
- `id` (ID!): ID único del proveedor (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener proveedores por tipo de servicio
```graphql
query {
  getProveedoresByTipoServicio(tipoServicio: "Hotel") {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Retorna todos los proveedores que ofrecen un tipo de servicio específico.

**Parámetros**:
- `tipoServicio` (String!): Tipo de servicio (Hotel, Vuelo, Transporte, etc.)

**Permisos**: Requiere autenticación

---

### 4️⃣ Buscar proveedores por nombre de empresa
```graphql
query {
  searchProveedores(searchTerm: "Hotel") {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Busca proveedores cuyo nombre de empresa contenga el término de búsqueda.

**Parámetros**:
- `searchTerm` (String!): Término a buscar en nombre de empresa

**Permisos**: Requiere autenticación

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nuevo proveedor
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Hotel Plaza La Paz S.A."
    tipoServicio: "Hotel"
    contactoNombre: "Roberto Sánchez"
    contactoEmail: "contacto@hotelplaza.com"
    contactoTelefono: "2-2345678"
  }) {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Crea un nuevo proveedor en el sistema.

**Input**:
- `nombreEmpresa` (String!): Nombre de la empresa proveedora
- `tipoServicio` (String!): Tipo de servicio que ofrece
- `contactoNombre` (String): Nombre de la persona de contacto (opcional)
- `contactoEmail` (String): Email de contacto (opcional)
- `contactoTelefono` (String): Teléfono de contacto (opcional)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El proveedor creado con todos sus datos

---

### 2️⃣ Actualizar proveedor
```graphql
mutation {
  updateProveedor(
    id: "673cd79d2e9af5567b9c0c01"
    input: {
      nombreEmpresa: "Hotel Plaza Premium La Paz S.A."
      contactoNombre: "Roberto Sánchez Flores"
      contactoEmail: "contacto.premium@hotelplaza.com"
      contactoTelefono: "2-2345679"
    }
  ) {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```
**Descripción**: Actualiza los datos de un proveedor existente.

**Parámetros**:
- `id` (ID!): ID del proveedor a actualizar

**Input** (todos opcionales):
- `nombreEmpresa` (String): Nuevo nombre de empresa
- `tipoServicio` (String): Nuevo tipo de servicio
- `contactoNombre` (String): Nuevo nombre de contacto
- `contactoEmail` (String): Nuevo email de contacto
- `contactoTelefono` (String): Nuevo teléfono de contacto

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El proveedor actualizado

---

### 3️⃣ Eliminar proveedor
```graphql
mutation {
  deleteProveedor(id: "673cd79d2e9af5567b9c0c01")
}
```
**Descripción**: Elimina un proveedor del sistema.

**Parámetros**:
- `id` (ID!): ID del proveedor a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

**⚠️ Nota**: Esta operación puede fallar si el proveedor tiene servicios asociados.

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Tipos de Servicio**: Los tipos de servicio más comunes son:
   - Hotel
   - Vuelo
   - Transporte
   - Restaurante
   - Tour/Excursión
   - Crucero
   - Alquiler de vehículos
   - Seguro de viaje

4. **Eliminación**: El `deleteProveedor` elimina físicamente el registro. Asegúrese de que no tenga servicios activos asociados.

5. **Validaciones**:
   - El nombre de empresa debe ser único
   - El tipo de servicio es requerido
   - Email debe tener formato válido (si se proporciona)

6. **Relación con Servicios**: Un proveedor puede tener múltiples servicios asociados.

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllProveedores | ❌ | ✅ | ✅ |
| getProveedorById | ❌ | ✅ | ✅ |
| getProveedoresByTipoServicio | ❌ | ✅ | ✅ |
| searchProveedores | ❌ | ✅ | ✅ |
| createProveedor | ❌ | ✅ | ✅ |
| updateProveedor | ❌ | ✅ | ✅ |
| deleteProveedor | ❌ | ❌ | ✅ |

---

## 🧪 Ejemplos de Uso Completo

### Crear proveedor de hotel
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Radisson Hotel Cochabamba"
    tipoServicio: "Hotel"
    contactoNombre: "Patricia Rojas"
    contactoEmail: "reservas@radissoncocha.com"
    contactoTelefono: "4-4567890"
  }) {
    id
    nombreEmpresa
    tipoServicio
  }
}
```

### Crear proveedor de aerolínea
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Boliviana de Aviación (BoA)"
    tipoServicio: "Vuelo"
    contactoNombre: "Luis Mendoza"
    contactoEmail: "ventas@boa.bo"
    contactoTelefono: "800-10-2000"
  }) {
    id
    nombreEmpresa
    tipoServicio
  }
}
```

### Buscar proveedores de hoteles
```graphql
# 1. Por tipo de servicio
query {
  getProveedoresByTipoServicio(tipoServicio: "Hotel") {
    id
    nombreEmpresa
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}

# 2. Por nombre
query {
  searchProveedores(searchTerm: "Radisson") {
    id
    nombreEmpresa
    tipoServicio
    contactoEmail
  }
}
```

### Actualizar información de contacto
```graphql
mutation {
  updateProveedor(
    id: "673cd79d2e9af5567b9c0c01"
    input: {
      contactoNombre: "Patricia Rojas Quispe"
      contactoEmail: "reservas.nuevas@radissoncocha.com"
      contactoTelefono: "4-4567891"
    }
  ) {
    id
    nombreEmpresa
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```

### Obtener todos los proveedores y filtrar por tipo
```graphql
query {
  getAllProveedores {
    id
    nombreEmpresa
    tipoServicio
    contactoNombre
    contactoEmail
    contactoTelefono
  }
}
```

---

## 🏪 Tipos de Proveedores Comunes

### 🏨 Hoteles
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Camino Real Suites"
    tipoServicio: "Hotel"
    contactoNombre: "María González"
    contactoEmail: "info@caminoreal.com"
    contactoTelefono: "2-2441515"
  }) {
    id
  }
}
```

### ✈️ Aerolíneas
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Amaszonas"
    tipoServicio: "Vuelo"
    contactoNombre: "Jorge Silva"
    contactoEmail: "ventas@amaszonas.com"
    contactoTelefono: "2-2110010"
  }) {
    id
  }
}
```

### 🚌 Transporte Terrestre
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Trans Copacabana"
    tipoServicio: "Transporte"
    contactoNombre: "Pedro Mamani"
    contactoEmail: "info@transcopacabana.com"
    contactoTelefono: "2-2374242"
  }) {
    id
  }
}
```

### 🍽️ Restaurantes
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Gustu Restaurant"
    tipoServicio: "Restaurante"
    contactoNombre: "Andrea López"
    contactoEmail: "reservas@gustu.bo"
    contactoTelefono: "2-2117491"
  }) {
    id
  }
}
```

### 🎭 Tours y Excursiones
```graphql
mutation {
  createProveedor(input: {
    nombreEmpresa: "Red Cap Walking Tours"
    tipoServicio: "Tour"
    contactoNombre: "Carlos Vera"
    contactoEmail: "info@redcaptours.com"
    contactoTelefono: "71234567"
  }) {
    id
  }
}
```
