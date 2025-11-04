# 🚀 Guía Rápida - Sistema Completo

## ✅ ¿Qué se ha creado?

Se ha implementado un sistema completo de autenticación y dashboard con:

### Frontend (Angular 19)
- ✅ Página de Login
- ✅ Página de Registro  
- ✅ Dashboard con Sidebar responsive
- ✅ Integración GraphQL con Apollo Client
- ✅ Servicio de autenticación
- ✅ Guards para protección de rutas
- ✅ Diseño moderno y profesional

### Componentes Creados

```
agencia-frontend/src/app/
├── guards/auth.guard.ts              # Protección de rutas
├── models/user.model.ts              # Interfaces TypeScript
├── services/auth.service.ts          # Lógica de autenticación
├── pages/
│   ├── login/                        # Componente de login
│   ├── register/                     # Componente de registro
│   └── dashboard/                    # Dashboard principal
└── graphql.module.ts                 # Configuración Apollo/GraphQL
```

## 🏃‍♂️ Cómo Ejecutar

### 1. Iniciar Backend (en una terminal)

```powershell
cd f:\JHOEL\SEMESTRE` 2-2025\SW2\EXAMEN1\agencia-backend
./mvnw spring-boot:run
```

El backend correrá en: `http://localhost:8080`

### 2. Iniciar Frontend (en otra terminal)

```powershell
cd f:\JHOEL\SEMESTRE` 2-2025\SW2\EXAMEN1\agencia-frontend
npm start
```

El frontend correrá en: `http://localhost:4200`

## 🎯 Flujo de Uso

### 1. Registro de Usuario

1. Abre `http://localhost:4200` (redirige automáticamente a `/login`)
2. Haz clic en "Regístrate aquí"
3. Completa el formulario:
   - Email (requerido)
   - Contraseña (requerido, mínimo 6 caracteres)
   - Nombre (requerido)
   - Apellido (requerido)
   - Teléfono (opcional)
   - Sexo (opcional: M/F)
4. Clic en "Registrarse"
5. Si es exitoso, se redirige automáticamente al dashboard

### 2. Inicio de Sesión

1. Ingresa a `http://localhost:4200/login`
2. Completa:
   - Email
   - Contraseña
3. Clic en "Iniciar Sesión"
4. Si es correcto, se redirige al dashboard

### 3. Dashboard

- **Sidebar**: Menú de navegación con iconos
- **Botón toggle**: Colapsa/expande el sidebar
- **Stats Cards**: Muestra estadísticas (datos de ejemplo)
- **Acciones Rápidas**: Botones para acciones comunes
- **Actividad Reciente**: Lista de actividades (datos de ejemplo)
- **Cerrar Sesión**: Botón en el footer del sidebar

## 🔐 Autenticación

### Cómo Funciona

1. **Registro/Login**: Envía petición GraphQL al backend
2. **Backend responde** con:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIs...",
     "type": "Bearer",
     "usuario": { ... }
   }
   ```
3. **Token se guarda** en localStorage
4. **Todas las peticiones** posteriores incluyen el token en headers:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   ```

### Protección de Rutas

```typescript
// En app.routes.ts
{ 
  path: 'dashboard', 
  component: DashboardComponent,
  canActivate: [authGuard]  // ← Solo usuarios autenticados
}
```

Si intentas acceder a `/dashboard` sin estar autenticado, te redirige a `/login`.

## 📊 Queries y Mutations GraphQL

### Login
```graphql
mutation Login($input: LoginInput!) {
  login(input: $input) {
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

### Registro
```graphql
mutation Register($input: RegisterInput!) {
  register(input: $input) {
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

### Obtener Usuario Actual
```graphql
query Me {
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

## 🎨 Características del Dashboard

### Sidebar
- **Responsive**: Se adapta a móviles
- **Colapsable**: Botón para minimizar
- **Items de menú**: Con iconos y labels
- **Footer**: Info de usuario + logout

### Menú Items
- 📊 Dashboard
- 👥 Clientes
- 📦 Paquetes
- 🏨 Servicios
- 💰 Ventas
- 🏢 Proveedores
- 👤 Agentes
- ⚙️ Configuración

*Nota: Las rutas de estos módulos están definidas pero los componentes aún no están implementados*

## 🔧 Configuración Importante

### URL del Backend

En `src/app/graphql.module.ts`:
```typescript
const uri = 'http://localhost:8080/graphql';
```

Si tu backend corre en otro puerto, cámbialo aquí.

### CORS

El backend ya tiene CORS configurado en `SecurityConfig.java` para aceptar peticiones desde `http://localhost:4200`.

## 🐛 Solución de Problemas

### Error: "Cannot connect to backend"

✅ **Solución**: 
1. Verifica que el backend esté corriendo
2. Abre `http://localhost:8080/graphql` en el navegador
3. Deberías ver el GraphQL Playground

### Error: "CORS policy"

✅ **Solución**: 
- Ya está configurado en `SecurityConfig.java`
- Verifica que estés usando `http://localhost:4200` (no 127.0.0.1)

### Error: "Invalid token"

✅ **Solución**:
1. Cierra sesión
2. Limpia localStorage: F12 → Application → Local Storage → Clear
3. Vuelve a iniciar sesión

### No se ve nada al iniciar

✅ **Solución**:
1. Verifica que ambos servidores estén corriendo
2. Abre la consola del navegador (F12)
3. Revisa errores en la pestaña Console y Network

## 📱 Responsive Design

El dashboard es completamente responsive:

- **Desktop (> 768px)**: Sidebar completo + contenido
- **Mobile (< 768px)**: Sidebar colapsado automáticamente
- **Touch**: Funciona perfectamente en tablets

## 🎯 Próximos Pasos Sugeridos

### 1. Módulo de Clientes
- Lista de clientes con GraphQL
- Formulario crear/editar cliente
- Ver detalles del cliente

### 2. Módulo de Paquetes
- CRUD de paquetes turísticos
- Gestión de servicios incluidos
- Precios y disponibilidad

### 3. Módulo de Ventas
- Registro de ventas
- Asignación a clientes
- Historial de transacciones

### 4. Dashboard Real
- Estadísticas desde el backend
- Gráficos con charts.js o ng2-charts
- Datos en tiempo real

## 📚 Recursos

- [Angular Docs](https://angular.dev)
- [Apollo Angular](https://apollo-angular.com)
- [GraphQL](https://graphql.org)
- [Spring Boot GraphQL](https://spring.io/projects/spring-graphql)

## 💡 Tips

1. **DevTools**: Instala [Apollo DevTools](https://chrome.google.com/webstore/detail/apollo-client-devtools) para Chrome
2. **GraphQL Playground**: Usa `http://localhost:8080/graphql` para probar queries
3. **Hot Reload**: Ambos servidores tienen hot reload, los cambios se reflejan automáticamente

## 🎉 ¡Listo!

Tu sistema está completamente funcional. Puedes:
- ✅ Registrar usuarios
- ✅ Iniciar sesión
- ✅ Acceder al dashboard protegido
- ✅ Ver información del usuario actual
- ✅ Cerrar sesión

**¡Ahora puedes comenzar a agregar más módulos y funcionalidades!** 🚀
