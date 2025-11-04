# Agencia de Viajes - Backend

Sistema de gestión empresarial para agencia de viajes con Spring Boot, GraphQL, MongoDB y Spring Security.

## Tecnologías

- **Java 21** (LTS)
- **Spring Boot 3.5.7**
- **MongoDB** - Base de datos NoSQL
- **Spring Security** - Autenticación y autorización
- **JWT** - JSON Web Tokens para autenticación
- **GraphQL** - API de consultas
- **Lombok** - Reducción de código boilerplate

## Estructura del Proyecto

```
src/main/java/com/agencia/agencia_backend/
├── model/              # Entidades de MongoDB
│   ├── Usuario.java
│   ├── Cliente.java
│   ├── Agente.java
│   ├── Proveedor.java
│   ├── Servicio.java
│   ├── PaqueteTuristico.java
│   ├── Venta.java
│   ├── DetalleVenta.java
│   └── PaqueteServicio.java
├── repository/         # Repositorios de MongoDB
├── service/           # Lógica de negocio
│   └── AuthService.java
├── controller/        # Controladores REST
│   ├── AuthController.java
│   └── TestController.java
├── security/          # Configuración de seguridad
│   ├── SecurityConfig.java
│   ├── JwtUtils.java
│   ├── AuthTokenFilter.java
│   ├── AuthEntryPointJwt.java
│   ├── UserDetailsImpl.java
│   └── UserDetailsServiceImpl.java
└── dto/              # Data Transfer Objects
    ├── LoginRequest.java
    ├── RegisterRequest.java
    └── AuthResponse.java
```

## Configuración Inicial

### 1. Instalar MongoDB

#### Windows (usando Chocolatey):
```powershell
choco install mongodb
```

#### O descargar manualmente:
- Descargar desde: https://www.mongodb.com/try/download/community
- Instalar y agregar al PATH

### 2. Iniciar MongoDB

```powershell
# Crear directorio para datos
mkdir C:\data\db

# Iniciar MongoDB
mongod
```

### 3. Configurar application.properties

El archivo `src/main/resources/application.properties` ya está configurado con:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/agencia_viajes
spring.data.mongodb.database=agencia_viajes

# JWT (CAMBIAR EN PRODUCCIÓN)
jwt.secret=tu_clave_secreta_super_segura_cambiala_en_produccion
jwt.expiration=86400000
```

**IMPORTANTE:** Cambia el `jwt.secret` por una clave más segura en producción.

## Ejecutar la Aplicación

### Usando Maven Wrapper:

```powershell
# Compilar el proyecto
.\mvnw clean install

# Ejecutar la aplicación
.\mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints de Autenticación

### 1. Registro de Usuario

**POST** `/api/auth/register`

```json
{
  "email": "usuario@example.com",
  "password": "password123",
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "123456789",
  "sexo": "M"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": "67890abc123def456",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "isAdmin": false,
  "isAgente": false,
  "isCliente": true
}
```

### 2. Login de Usuario

**POST** `/api/auth/login`

```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Respuesta:** (Igual que el registro)

### 3. Endpoints de Prueba

#### Acceso Público (sin autenticación)
**GET** `/api/test/public`

#### Acceso Autenticado
**GET** `/api/test/user`
- Headers: `Authorization: Bearer {token}`

#### Acceso Admin
**GET** `/api/test/admin`
- Headers: `Authorization: Bearer {token}`
- Requiere rol: ADMIN

#### Acceso Agente
**GET** `/api/test/agente`
- Headers: `Authorization: Bearer {token}`
- Requiere rol: ADMIN o AGENTE

#### Acceso Cliente
**GET** `/api/test/cliente`
- Headers: `Authorization: Bearer {token}`
- Requiere rol: ADMIN, AGENTE o CLIENTE

## Roles de Usuario

El sistema maneja tres tipos de roles:

1. **ADMIN** (`isAdmin: true`)
   - Acceso total al sistema
   - Gestión de usuarios, agentes y clientes

2. **AGENTE** (`isAgente: true`)
   - Gestión de ventas y reservas
   - Gestión de clientes
   - Acceso a reportes

3. **CLIENTE** (`isCliente: true`)
   - Ver sus propias reservas
   - Realizar cotizaciones
   - Gestionar su perfil

## Probar con Postman o Thunder Client

### 1. Registrar un usuario:
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@test.com",
  "password": "password123",
  "nombre": "Test",
  "apellido": "Usuario",
  "telefono": "123456789",
  "sexo": "M"
}
```

### 2. Copiar el token de la respuesta

### 3. Usar el token en requests protegidos:
```
GET http://localhost:8080/api/test/user
Authorization: Bearer {tu_token_aquí}
```

## Crear un Usuario Admin Manualmente

Conéctate a MongoDB y ejecuta:

```javascript
use agencia_viajes

db.usuarios.insertOne({
  email: "admin@agencia.com",
  password: "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", // password: admin123
  nombre: "Admin",
  apellido: "Principal",
  telefono: "999999999",
  sexo: "M",
  isAdmin: true,
  isAgente: false,
  isCliente: false,
  isActive: true
})
```

Luego puedes hacer login con:
- Email: `admin@agencia.com`
- Password: `admin123`

## Próximos Pasos

1. **Implementar GraphQL**
   - Definir schemas en `src/main/resources/graphql/`
   - Crear resolvers para las entidades

2. **Crear Servicios de Negocio**
   - `ClienteService`
   - `AgenteService`
   - `VentaService`
   - `ServicioService`
   - `PaqueteService`

3. **Implementar Controladores REST**
   - Controllers para cada entidad
   - Validaciones y manejo de errores

4. **Agregar Validaciones**
   - Validaciones de negocio
   - Manejo de excepciones personalizado

5. **Documentación API**
   - Agregar Swagger/OpenAPI
   - Documentar todos los endpoints

## Base de Datos

### Colecciones MongoDB:

- **usuarios** - Información de autenticación y roles
- **clientes** - Información adicional de clientes
- **agentes** - Información de empleados/agentes
- **proveedores** - Empresas que proveen servicios
- **servicios** - Vuelos, hoteles, tours, etc.
- **paquetesTuristicos** - Paquetes de servicios combinados
- **ventas** - Transacciones de venta
- **detalleVenta** - Líneas de detalle de cada venta
- **paqueteServicios** - Relación muchos a muchos entre paquetes y servicios

## Solución de Problemas

### MongoDB no conecta:
```powershell
# Verificar si MongoDB está corriendo
Get-Process mongod

# Si no está corriendo, iniciarlo:
mongod
```

### Puerto 8080 en uso:
Cambiar el puerto en `application.properties`:
```properties
server.port=8081
```

### Errores de compilación:
```powershell
.\mvnw clean install -U
```

## Contacto y Soporte

Para dudas o problemas, revisar los logs en la consola o contactar al equipo de desarrollo.
