# 👨‍💼 Queries y Mutations - Agentes

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Agentes** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todos los agentes
```graphql
query {
  getAllAgentes {
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
      isAgente
    }
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Retorna la lista completa de todos los agentes registrados en el sistema.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener agente por ID
```graphql
query {
  getAgenteById(id: "673cd79d2e9af5567b9c0bf8") {
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
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Retorna un agente específico basado en su ID.

**Parámetros**:
- `id` (ID!): ID único del agente (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener agente por ID de usuario
```graphql
query {
  getAgenteByUsuarioId(usuarioId: "673cd79d2e9af5567b9c0bf9") {
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
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Retorna el agente asociado a un ID de usuario específico.

**Parámetros**:
- `usuarioId` (ID!): ID del usuario relacionado

**Permisos**: Requiere autenticación

---

### 4️⃣ Buscar agentes por nombre o apellido
```graphql
query {
  searchAgentes(searchTerm: "Martínez") {
    id
    usuarioId
    usuario {
      nombre
      apellido
      email
      telefono
    }
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Busca agentes cuyo nombre o apellido contenga el término de búsqueda.

**Parámetros**:
- `searchTerm` (String!): Término a buscar en nombre o apellido

**Permisos**: Requiere autenticación (Admin o Agente)

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nuevo agente
```graphql
mutation {
  createAgente(input: {
    email: "carlos.martinez@agencia.com"
    password: "agente2024"
    nombre: "Carlos"
    apellido: "Martínez"
    telefono: "70123456"
    sexo: "M"
    puesto: "Agente de Ventas Senior"
    fechaContratacion: "2024-01-15"
  }) {
    id
    usuarioId
    usuario {
      id
      email
      nombre
      apellido
      isAgente
      isActive
    }
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Crea un nuevo agente en el sistema (incluye la creación del usuario asociado).

**Input**:
- `email` (String!): Correo electrónico único
- `password` (String!): Contraseña (será encriptada)
- `nombre` (String!): Nombre del agente
- `apellido` (String!): Apellido del agente
- `telefono` (String): Número de teléfono (opcional)
- `sexo` (String): Género ('M' o 'F') (opcional)
- `puesto` (String!): Puesto o cargo del agente
- `fechaContratacion` (String): Fecha de contratación en formato ISO (opcional)

**Permisos**: Requiere autenticación (Admin)

**Retorna**: El agente creado con todos sus datos

---

### 2️⃣ Actualizar agente
```graphql
mutation {
  updateAgente(
    id: "673cd79d2e9af5567b9c0bf8"
    input: {
      puesto: "Agente de Ventas Manager"
      fechaContratacion: "2024-01-20"
    }
  ) {
    id
    usuarioId
    usuario {
      nombre
      apellido
      email
    }
    puesto
    fechaContratacion
  }
}
```
**Descripción**: Actualiza los datos de un agente existente.

**Parámetros**:
- `id` (ID!): ID del agente a actualizar

**Input**:
- `puesto` (String): Nuevo puesto (opcional)
- `fechaContratacion` (String): Nueva fecha de contratación (opcional)

**Permisos**: Requiere autenticación (Admin)

**Retorna**: El agente actualizado

---

### 3️⃣ Eliminar agente
```graphql
mutation {
  deleteAgente(id: "673cd79d2e9af5567b9c0bf8")
}
```
**Descripción**: Elimina lógicamente un agente del sistema (marca como inactivo).

**Parámetros**:
- `id` (ID!): ID del agente a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

---

### 4️⃣ Activar/Desactivar agente
```graphql
mutation {
  toggleAgenteStatus(id: "673cd79d2e9af5567b9c0bf8") {
    id
    usuario {
      nombre
      apellido
      isActive
    }
    puesto
  }
}
```
**Descripción**: Alterna el estado activo/inactivo de un agente.

**Parámetros**:
- `id` (ID!): ID del agente

**Permisos**: Requiere autenticación (Admin)

**Retorna**: El agente con su nuevo estado

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Relación Usuario-Agente**: Cada agente está vinculado a un usuario. El usuario contiene información de login y datos personales, mientras que el agente contiene información laboral específica.

4. **Eliminación**: El `deleteAgente` no elimina físicamente el registro, solo marca el usuario asociado como inactivo (`isActive: false`).

5. **Validaciones**:
   - Email debe ser único en el sistema
   - El campo `puesto` es obligatorio al crear
   - Los campos requeridos no pueden estar vacíos

6. **Fechas**: Las fechas se manejan en formato ISO 8601 (YYYY-MM-DD)

7. **Permisos especiales**: Solo los administradores pueden crear, actualizar o eliminar agentes.

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllAgentes | ❌ | ✅ | ✅ |
| getAgenteById | ❌ | ✅* | ✅ |
| getAgenteByUsuarioId | ❌ | ✅* | ✅ |
| searchAgentes | ❌ | ✅ | ✅ |
| createAgente | ❌ | ❌ | ✅ |
| updateAgente | ❌ | ❌ | ✅ |
| deleteAgente | ❌ | ❌ | ✅ |
| toggleAgenteStatus | ❌ | ❌ | ✅ |

*Los agentes pueden ver su propia información

---

## 🧪 Ejemplos de Uso Completo

### Crear agente y consultar sus datos
```graphql
# 1. Crear agente
mutation {
  createAgente(input: {
    email: "ana.torres@agencia.com"
    password: "agentePass123"
    nombre: "Ana"
    apellido: "Torres"
    telefono: "71234567"
    sexo: "F"
    puesto: "Agente de Ventas Junior"
    fechaContratacion: "2024-11-01"
  }) {
    id
    usuarioId
    usuario {
      id
      email
      isAgente
    }
    puesto
    fechaContratacion
  }
}

# 2. Consultar el agente creado
query {
  getAgenteById(id: "ID_OBTENIDO_ARRIBA") {
    id
    usuario {
      nombre
      apellido
      email
      telefono
      isActive
    }
    puesto
    fechaContratacion
  }
}
```

### Buscar y actualizar agente
```graphql
# 1. Buscar agente
query {
  searchAgentes(searchTerm: "Torres") {
    id
    usuario {
      nombre
      apellido
      email
    }
    puesto
    fechaContratacion
  }
}

# 2. Actualizar puesto del agente
mutation {
  updateAgente(
    id: "ID_OBTENIDO_ARRIBA"
    input: {
      puesto: "Agente de Ventas Senior"
    }
  ) {
    id
    usuario {
      nombre
      apellido
    }
    puesto
  }
}
```

### Gestionar estado del agente
```graphql
# Desactivar agente temporalmente
mutation {
  toggleAgenteStatus(id: "673cd79d2e9af5567b9c0bf8") {
    id
    usuario {
      nombre
      apellido
      isActive
    }
    puesto
  }
}

# Reactivar agente
mutation {
  toggleAgenteStatus(id: "673cd79d2e9af5567b9c0bf8") {
    id
    usuario {
      nombre
      apellido
      isActive
    }
    puesto
  }
}
```

---

## 💼 Puestos Comunes

Ejemplos de puestos típicos en una agencia de viajes:
- Agente de Ventas Junior
- Agente de Ventas Senior
- Agente de Ventas Manager
- Coordinador de Viajes
- Especialista en Paquetes Turísticos
- Asesor de Viajes Corporativos
- Supervisor de Agentes
