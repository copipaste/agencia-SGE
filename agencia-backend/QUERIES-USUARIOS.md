# 📚 Documentación de Queries y Mutations - USUARIOS

## 📖 Índice
- [Queries (Consultas)](#queries-consultas)
  - [me - Obtener usuario actual autenticado](#me---obtener-usuario-actual-autenticado)
- [Tipos de Datos](#tipos-de-datos)
- [Notas Importantes](#notas-importantes)

---

## 🔍 Queries (Consultas)

### `me` - Obtener usuario actual autenticado

Obtiene la información del usuario que está actualmente autenticado mediante el token JWT.

#### Query
```graphql
query {
  me {
    id
    email
    nombre
    apellido
    telefono
    sexo
    isAdmin
    isAgente
    isCliente
    isActive
  }
}
```

#### Respuesta Exitosa
```json
{
  "data": {
    "me": {
      "id": "673ebe1a9f2e3c4d5a6b7c8d",
      "email": "juan.perez@example.com",
      "nombre": "Juan",
      "apellido": "Pérez",
      "telefono": "+591 70123456",
      "sexo": "Masculino",
      "isAdmin": false,
      "isAgente": true,
      "isCliente": false,
      "isActive": true
    }
  }
}
```

#### Respuesta cuando no está autenticado
```json
{
  "errors": [
    {
      "message": "Unauthorized",
      "extensions": {
        "classification": "UNAUTHORIZED"
      }
    }
  ]
}
```

#### Ejemplo de uso (con header de autenticación)
```bash
# Usando curl
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{"query":"query { me { id email nombre apellido isAdmin isAgente isCliente } }"}'
```

#### Ejemplo en GraphQL Playground/Apollo
```graphql
# HTTP Headers (en la pestaña de headers)
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# Query
query GetCurrentUser {
  me {
    id
    email
    nombre
    apellido
    telefono
    sexo
    isAdmin
    isAgente
    isCliente
    isActive
  }
}
```

---

## 📦 Tipos de Datos

### Usuario
Representa un usuario del sistema con sus roles y permisos.

```graphql
type Usuario {
  id: ID!
  email: String!
  nombre: String!
  apellido: String!
  telefono: String
  sexo: String
  isAdmin: Boolean!
  isAgente: Boolean!
  isCliente: Boolean!
  isActive: Boolean!
}
```

#### Campos del Usuario

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `ID!` | Identificador único del usuario (MongoDB ObjectId) |
| `email` | `String!` | Correo electrónico único del usuario |
| `nombre` | `String!` | Nombre(s) del usuario |
| `apellido` | `String!` | Apellido(s) del usuario |
| `telefono` | `String` | Número de teléfono (opcional) |
| `sexo` | `String` | Sexo del usuario (Masculino/Femenino/Otro) |
| `isAdmin` | `Boolean!` | Indica si el usuario tiene privilegios de administrador |
| `isAgente` | `Boolean!` | Indica si el usuario es un agente de ventas |
| `isCliente` | `Boolean!` | Indica si el usuario es un cliente |
| `isActive` | `Boolean!` | Indica si la cuenta del usuario está activa |

---

## 📝 Notas Importantes

### 🔒 Autenticación Requerida
La query `me` **requiere** que el usuario esté autenticado. Debes incluir el token JWT en el header de la petición:

```
Authorization: Bearer <token_jwt>
```

### 👥 Roles de Usuario
Un usuario puede tener múltiples roles simultáneamente:
- **Admin** (`isAdmin: true`): Acceso completo al sistema
- **Agente** (`isAgente: true`): Puede gestionar ventas y clientes
- **Cliente** (`isCliente: true`): Puede realizar compras y consultar sus datos

### 🔑 Casos de Uso Comunes

#### 1. Verificar autenticación al cargar la aplicación
```graphql
query CheckAuth {
  me {
    id
    email
    nombre
    isActive
  }
}
```

#### 2. Obtener roles del usuario actual
```graphql
query GetUserRoles {
  me {
    id
    email
    isAdmin
    isAgente
    isCliente
  }
}
```

#### 3. Perfil de usuario completo
```graphql
query GetUserProfile {
  me {
    id
    email
    nombre
    apellido
    telefono
    sexo
    isAdmin
    isAgente
    isCliente
    isActive
  }
}
```

### ⚠️ Manejo de Errores

| Error | Causa | Solución |
|-------|-------|----------|
| `Unauthorized` | Token no proporcionado o inválido | Incluir header `Authorization: Bearer <token>` válido |
| `Token expired` | El token JWT ha expirado | Realizar login nuevamente para obtener nuevo token |
| `User not found` | El usuario del token no existe en BD | El usuario pudo haber sido eliminado |
| `User inactive` | La cuenta del usuario está desactivada | Contactar al administrador |

### 🔄 Relación con otras entidades

El `Usuario` se relaciona con:
- **Cliente**: Un usuario con `isCliente: true` tiene un registro asociado en la colección `Cliente`
- **Agente**: Un usuario con `isAgente: true` tiene un registro asociado en la colección `Agente`

Para obtener información completa del cliente o agente, usar las queries específicas:
```graphql
# Si es cliente
query {
  getClienteByUsuarioId(usuarioId: "USER_ID") {
    id
    direccion
    numeroPasaporte
    usuario {
      nombre
      apellido
      email
    }
  }
}

# Si es agente
query {
  getAgenteByUsuarioId(usuarioId: "USER_ID") {
    id
    puesto
    fechaContratacion
    usuario {
      nombre
      apellido
      email
    }
  }
}
```

---

## 🎯 Ejemplos Prácticos

### Ejemplo 1: Guard de autenticación en frontend
```typescript
// auth.guard.ts
async canActivate(): Promise<boolean> {
  try {
    const result = await this.apollo.query({
      query: gql`
        query {
          me {
            id
            isActive
          }
        }
      `
    }).toPromise();
    
    return result.data.me.isActive;
  } catch (error) {
    this.router.navigate(['/login']);
    return false;
  }
}
```

### Ejemplo 2: Verificar roles del usuario
```typescript
// role.service.ts
async hasRole(role: 'admin' | 'agente' | 'cliente'): Promise<boolean> {
  const result = await this.apollo.query({
    query: gql`
      query {
        me {
          isAdmin
          isAgente
          isCliente
        }
      }
    `
  }).toPromise();
  
  const user = result.data.me;
  
  switch(role) {
    case 'admin': return user.isAdmin;
    case 'agente': return user.isAgente;
    case 'cliente': return user.isCliente;
    default: return false;
  }
}
```

### Ejemplo 3: Mostrar información de perfil
```typescript
// profile.component.ts
loadUserProfile() {
  this.apollo.query({
    query: gql`
      query {
        me {
          id
          email
          nombre
          apellido
          telefono
          sexo
          isAdmin
          isAgente
          isCliente
        }
      }
    `
  }).subscribe(result => {
    this.currentUser = result.data.me;
    console.log('Usuario actual:', this.currentUser);
  });
}
```

---

**Última actualización:** 2025-11-02
**Versión:** 1.0.0
