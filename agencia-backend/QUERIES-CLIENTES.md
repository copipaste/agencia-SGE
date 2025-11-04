# 📋 Queries y Mutations - Clientes

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Clientes** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todos los clientes
```graphql
query {
  getAllClientes {
    id
    usuarioId
    usuario {
      id
      email
      nombre
      apellido
      telefono
      sexo
      isActive
    }
    direccion
    fechaNacimiento
    numeroPasaporte
  }
}
```
**Descripción**: Retorna la lista completa de todos los clientes registrados en el sistema.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener cliente por ID
```graphql
query {
  getClienteById(id: "673cd79d2e9af5567b9c0bf7") {
    id
    usuarioId
    usuario {
      id
      email
      nombre
      apellido
      telefono
      sexo
      isActive
    }
    direccion
    fechaNacimiento
    numeroPasaporte
  }
}
```
**Descripción**: Retorna un cliente específico basado en su ID.

**Parámetros**:
- `id` (ID!): ID único del cliente (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener cliente por ID de usuario
```graphql
query {
  getClienteByUsuarioId(usuarioId: "673cd79d2e9af5567b9c0bf6") {
    id
    usuarioId
    usuario {
      id
      email
      nombre
      apellido
      telefono
      sexo
      isActive
    }
    direccion
    fechaNacimiento
    numeroPasaporte
  }
}
```
**Descripción**: Retorna el cliente asociado a un ID de usuario específico.

**Parámetros**:
- `usuarioId` (ID!): ID del usuario relacionado

**Permisos**: Requiere autenticación

---

### 4️⃣ Buscar clientes por nombre o apellido
```graphql
query {
  searchClientes(searchTerm: "García") {
    id
    usuarioId
    usuario {
      nombre
      apellido
      email
      telefono
    }
    direccion
    numeroPasaporte
  }
}
```
**Descripción**: Busca clientes cuyo nombre o apellido contenga el término de búsqueda.

**Parámetros**:
- `searchTerm` (String!): Término a buscar en nombre o apellido

**Permisos**: Requiere autenticación (Admin o Agente)

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nuevo cliente
```graphql
mutation {
  createCliente(input: {
    email: "juan.perez@email.com"
    password: "password123"
    nombre: "Juan"
    apellido: "Pérez"
    telefono: "78945612"
    sexo: "M"
    direccion: "Av. Siempre Viva 123, La Paz"
    fechaNacimiento: "1990-05-15"
    numeroPasaporte: "ABC123456"
  }) {
    id
    usuarioId
    usuario {
      id
      email
      nombre
      apellido
      isCliente
      isActive
    }
    direccion
    fechaNacimiento
    numeroPasaporte
  }
}
```
**Descripción**: Crea un nuevo cliente en el sistema (incluye la creación del usuario asociado).

**Input**:
- `email` (String!): Correo electrónico único
- `password` (String!): Contraseña (será encriptada)
- `nombre` (String!): Nombre del cliente
- `apellido` (String!): Apellido del cliente
- `telefono` (String): Número de teléfono (opcional)
- `sexo` (String): Género ('M' o 'F') (opcional)
- `direccion` (String!): Dirección completa
- `fechaNacimiento` (String): Fecha de nacimiento en formato ISO (opcional)
- `numeroPasaporte` (String!): Número de pasaporte único

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El cliente creado con todos sus datos

---

### 2️⃣ Actualizar cliente
```graphql
mutation {
  updateCliente(
    id: "673cd79d2e9af5567b9c0bf7"
    input: {
      direccion: "Nueva Av. Principal 456, La Paz"
      fechaNacimiento: "1990-06-20"
      numeroPasaporte: "XYZ789012"
    }
  ) {
    id
    usuarioId
    usuario {
      nombre
      apellido
      email
    }
    direccion
    fechaNacimiento
    numeroPasaporte
  }
}
```
**Descripción**: Actualiza los datos de un cliente existente.

**Parámetros**:
- `id` (ID!): ID del cliente a actualizar

**Input**:
- `direccion` (String): Nueva dirección (opcional)
- `fechaNacimiento` (String): Nueva fecha de nacimiento (opcional)
- `numeroPasaporte` (String): Nuevo número de pasaporte (opcional)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El cliente actualizado

---

### 3️⃣ Eliminar cliente (Soft Delete)
```graphql
mutation {
  deleteCliente(id: "673cd79d2e9af5567b9c0bf7")
}
```
**Descripción**: Realiza una eliminación lógica del cliente (marca como inactivo en lugar de eliminar físicamente).

**Parámetros**:
- `id` (ID!): ID del cliente a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

---

### 4️⃣ Activar/Desactivar cliente
```graphql
mutation {
  toggleClienteStatus(id: "673cd79d2e9af5567b9c0bf7") {
    id
    usuario {
      nombre
      apellido
      isActive
    }
  }
}
```
**Descripción**: Alterna el estado activo/inactivo de un cliente.

**Parámetros**:
- `id` (ID!): ID del cliente

**Permisos**: Requiere autenticación (Admin)

**Retorna**: El cliente con su nuevo estado

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Relación Usuario-Cliente**: Cada cliente está vinculado a un usuario. El usuario contiene información de login y datos personales, mientras que el cliente contiene información específica del cliente.

4. **Eliminación**: El `deleteCliente` no elimina físicamente el registro, solo marca el usuario asociado como inactivo (`isActive: false`).

5. **Validaciones**:
   - Email debe ser único en el sistema
   - Número de pasaporte debe ser único
   - Los campos requeridos no pueden estar vacíos

6. **Fechas**: Las fechas se manejan en formato ISO 8601 (YYYY-MM-DD)

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllClientes | ❌ | ✅ | ✅ |
| getClienteById | ✅* | ✅ | ✅ |
| getClienteByUsuarioId | ✅* | ✅ | ✅ |
| searchClientes | ❌ | ✅ | ✅ |
| createCliente | ❌ | ✅ | ✅ |
| updateCliente | ❌ | ✅ | ✅ |
| deleteCliente | ❌ | ❌ | ✅ |
| toggleClienteStatus | ❌ | ❌ | ✅ |

*Solo puede ver su propia información

---

## 🧪 Ejemplos de Uso Completo

### Crear cliente y hacer login
```graphql
# 1. Crear cliente
mutation {
  createCliente(input: {
    email: "maria.lopez@email.com"
    password: "securePass123"
    nombre: "María"
    apellido: "López"
    telefono: "77889900"
    sexo: "F"
    direccion: "Calle Principal 789, Cochabamba"
    fechaNacimiento: "1995-03-10"
    numeroPasaporte: "PAS987654"
  }) {
    id
    usuarioId
  }
}

# 2. Hacer login
mutation {
  login(input: {
    email: "maria.lopez@email.com"
    password: "securePass123"
  }) {
    token
    type
    usuario {
      id
      nombre
      apellido
      isCliente
    }
  }
}
```

### Consultar y actualizar cliente
```graphql
# 1. Buscar cliente
query {
  searchClientes(searchTerm: "López") {
    id
    usuario {
      nombre
      apellido
      email
    }
    direccion
  }
}

# 2. Actualizar dirección
mutation {
  updateCliente(
    id: "ID_OBTENIDO_ARRIBA"
    input: {
      direccion: "Nueva dirección actualizada"
    }
  ) {
    id
    direccion
  }
}
```
