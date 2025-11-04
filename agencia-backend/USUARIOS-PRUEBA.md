# 🌱 Usuarios de Prueba (Seeder)

## ✅ Seeder Ejecutado Exitosamente

El sistema incluye un **DataSeeder** que crea automáticamente usuarios de prueba al iniciar la aplicación.

---

## 👥 Usuarios Creados

### 1️⃣ **ADMINISTRADOR**
```
📧 Email:    admin@agencia.com
🔑 Password: admin123
👤 Nombre:   Administrador Sistema
📱 Teléfono: 77777777
🎭 Rol:      ADMIN
✅ Activo:   Sí
```

**Permisos:**
- ✅ Acceso total al sistema
- ✅ Gestión de usuarios
- ✅ Gestión de clientes, agentes, servicios
- ✅ Gestión de ventas
- ✅ Configuración del sistema

---

### 2️⃣ **AGENTE**
```
📧 Email:       agente@agencia.com
🔑 Password:    agente123
👤 Nombre:      Carlos Rodríguez
📱 Teléfono:    76543210
🎭 Rol:         AGENTE
✅ Activo:      Sí

📋 Perfil de Agente:
   - Código:        AG-001
   - Especialidad:  Paquetes turísticos internacionales
   - Comisión:      10%
   - Contratación:  15/01/2024
```

**Permisos:**
- ✅ Gestión de clientes (crear, ver, editar)
- ✅ Gestión de servicios
- ✅ Gestión de paquetes turísticos
- ✅ Registro de ventas
- ✅ Ver reportes de sus ventas
- ❌ No puede eliminar usuarios
- ❌ No puede ver todos los agentes

---

### 3️⃣ **CLIENTE**
```
📧 Email:          cliente@agencia.com
🔑 Password:       cliente123
👤 Nombre:         María González
📱 Teléfono:       78901234
🎭 Rol:            CLIENTE
✅ Activo:         Sí

📋 Perfil de Cliente:
   - Dirección:           Av. Cristo Redentor #1234
   - Ciudad:              Santa Cruz
   - País:                Bolivia
   - Código Postal:       0000
   - Fecha Nacimiento:    15/06/1990
   - Documento:           7654321
   - Tipo Documento:      CI
   - Preferencias:        Playas, turismo de aventura, ecoturismo
```

**Permisos:**
- ✅ Ver su propio perfil
- ✅ Editar su propio perfil
- ✅ Ver paquetes turísticos disponibles
- ✅ Realizar reservas/compras
- ✅ Ver historial de sus compras
- ❌ No puede ver otros clientes
- ❌ No puede acceder a administración

---

## 🔐 Cómo Usar (GraphQL)

### Paso 1: Login con cualquier usuario

**Ejemplo con AGENTE:**
```graphql
mutation {
  login(input: {
    email: "agente@agencia.com"
    password: "agente123"
  }) {
    token
    type
    usuario {
      id
      email
      nombre
      apellido
      isAdmin
      isAgente
      isCliente
    }
  }
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
        "id": "673422...",
        "email": "agente@agencia.com",
        "nombre": "Carlos",
        "apellido": "Rodríguez",
        "isAdmin": false,
        "isAgente": true,
        "isCliente": false
      }
    }
  }
}
```

### Paso 2: Copiar el Token

Copia el valor de `token` de la respuesta.

### Paso 3: Configurar Headers en GraphiQL

En GraphiQL (http://localhost:8080/graphiql), en la sección "Headers":

```json
{
  "Authorization": "Bearer TU_TOKEN_AQUI"
}
```

### Paso 4: Realizar Consultas

Ahora puedes hacer consultas según el rol del usuario:

```graphql
query {
  me {
    id
    email
    nombre
    apellido
    isAdmin
    isAgente
    isCliente
  }
}
```

---

## 📊 Matriz de Permisos

| Operación | ADMIN | AGENTE | CLIENTE |
|-----------|-------|--------|---------|
| Ver todos los clientes | ✅ | ✅ | ❌ |
| Ver cliente específico | ✅ | ✅ | ✅* |
| Crear cliente | ✅ | ✅ | ❌ |
| Editar cliente | ✅ | ✅ | ✅* |
| Eliminar cliente | ✅ | ❌ | ❌ |
| Buscar clientes | ✅ | ✅ | ❌ |

*Solo su propio perfil

---

## 🛠️ Código del Seeder

El seeder está ubicado en:
```
src/main/java/com/agencia/agencia_backend/config/DataSeeder.java
```

### Características:
- ✅ Se ejecuta automáticamente al iniciar la aplicación
- ✅ Verifica si los usuarios ya existen (no duplica)
- ✅ Encripta las contraseñas con BCrypt
- ✅ Crea perfiles completos (Usuario + Cliente/Agente)
- ✅ Usa `CommandLineRunner` de Spring Boot

### Logs del Seeder:
```
🌱 Iniciando Seeder de datos...
👤 Usuario ADMIN creado: admin@agencia.com / admin123
👤 Usuario AGENTE creado: agente@agencia.com / agente123
   - Código: AG-001
   - Especialidad: Paquetes turísticos internacionales
👤 Usuario CLIENTE creado: cliente@agencia.com / cliente123
   - Nombre: María González
   - Ciudad: Santa Cruz, Bolivia
   - Preferencias: Playas, turismo de aventura, ecoturismo
✅ Seeder completado exitosamente!
```

O si ya existen:
```
🌱 Iniciando Seeder de datos...
⚠️  Usuario ADMIN ya existe
⚠️  Usuario AGENTE ya existe
⚠️  Usuario CLIENTE ya existe
✅ Seeder completado exitosamente!
```

---

## 🔄 Cómo Reiniciar los Datos

Si quieres volver a crear los usuarios desde cero:

### Opción 1: Eliminar desde MongoDB Atlas
1. Ir a MongoDB Atlas
2. Colecciones → `usuarios`, `clientes`, `agentes`
3. Eliminar los documentos
4. Reiniciar la aplicación

### Opción 2: Modificar el Seeder
Cambiar los emails en `DataSeeder.java` y reiniciar.

---

## 📝 Notas Importantes

1. **Seguridad:** Las contraseñas están encriptadas con BCrypt
2. **JWT:** Los tokens expiran en 24 horas
3. **Roles:** Un usuario puede tener múltiples roles (ej: Admin + Agente)
4. **Perfiles:** Cliente y Agente tienen perfiles separados con datos adicionales
5. **Validaciones:** Todos los endpoints GraphQL validan el rol del usuario

---

## 🧪 Casos de Prueba Sugeridos

### Como ADMIN:
1. Login como admin
2. Ver todos los clientes: `getAllClientes`
3. Crear nuevo agente
4. Ver estadísticas globales

### Como AGENTE:
1. Login como agente
2. Ver lista de clientes
3. Crear nuevo cliente
4. Registrar una venta
5. Ver mis comisiones

### Como CLIENTE:
1. Login como cliente
2. Ver mi perfil: `me`
3. Actualizar mis preferencias de viaje
4. Ver paquetes disponibles
5. Realizar una reserva

---

## 🎯 Próximos Pasos

Con estos usuarios de prueba puedes:
1. ✅ Probar todos los endpoints de autenticación
2. ✅ Probar el CRUD de clientes
3. ✅ Implementar CRUD de Servicios
4. ✅ Implementar CRUD de Paquetes Turísticos
5. ✅ Implementar CRUD de Ventas
6. ✅ Desarrollar el frontend con usuarios reales

---

¡Los usuarios están listos para usar! 🚀
