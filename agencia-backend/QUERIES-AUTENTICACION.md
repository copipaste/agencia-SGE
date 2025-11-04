# 🔐 Documentación de Autenticación - GraphQL API

## 📖 Índice
- [Mutations de Autenticación](#mutations-de-autenticación)
  - [register - Registrar nuevo usuario](#register---registrar-nuevo-usuario)
  - [login - Iniciar sesión](#login---iniciar-sesión)
- [Tipos de Datos](#tipos-de-datos)
- [Guía de Implementación](#guía-de-implementación)
- [Seguridad y Mejores Prácticas](#seguridad-y-mejores-prácticas)

---

## 🔓 Mutations de Autenticación

### `register` - Registrar nuevo usuario

Crea una nueva cuenta de usuario en el sistema. Por defecto, los usuarios registrados son clientes.

#### Mutation
```graphql
mutation {
  register(input: {
    email: "juan.perez@example.com"
    password: "SecurePass123!"
    nombre: "Juan"
    apellido: "Pérez"
    telefono: "+591 70123456"
    sexo: "Masculino"
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
      isActive
    }
  }
}
```

#### Respuesta Exitosa
```json
{
  "data": {
    "register": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NzNlYmUxYTlmMmUzYzRkNWE2YjdjOGQiLCJlbWFpbCI6Imp1YW4ucGVyZXpAZXhhbXBsZS5jb20iLCJpYXQiOjE3MzA1MDAwMDAsImV4cCI6MTczMDU4NjQwMH0.signature",
      "type": "Bearer",
      "usuario": {
        "id": "673ebe1a9f2e3c4d5a6b7c8d",
        "email": "juan.perez@example.com",
        "nombre": "Juan",
        "apellido": "Pérez",
        "isAdmin": false,
        "isAgente": false,
        "isCliente": true,
        "isActive": true
      }
    }
  }
}
```

#### Posibles Errores
```json
{
  "errors": [
    {
      "message": "El email ya está registrado",
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ]
}
```

```json
{
  "errors": [
    {
      "message": "La contraseña debe tener al menos 6 caracteres",
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ]
}
```

---

### `login` - Iniciar sesión

Autentica a un usuario existente y devuelve un token JWT.

#### Mutation
```graphql
mutation {
  login(input: {
    email: "juan.perez@example.com"
    password: "SecurePass123!"
  }) {
    token
    type
    usuario {
      id
      email
      nombre
      apellido
      telefono
      isAdmin
      isAgente
      isCliente
      isActive
    }
  }
}
```

#### Respuesta Exitosa
```json
{
  "data": {
    "login": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NzNlYmUxYTlmMmUzYzRkNWE2YjdjOGQiLCJlbWFpbCI6Imp1YW4ucGVyZXpAZXhhbXBsZS5jb20iLCJpYXQiOjE3MzA1MDAwMDAsImV4cCI6MTczMDU4NjQwMH0.signature",
      "type": "Bearer",
      "usuario": {
        "id": "673ebe1a9f2e3c4d5a6b7c8d",
        "email": "juan.perez@example.com",
        "nombre": "Juan",
        "apellido": "Pérez",
        "telefono": "+591 70123456",
        "isAdmin": false,
        "isAgente": true,
        "isCliente": false,
        "isActive": true
      }
    }
  }
}
```

#### Posibles Errores
```json
{
  "errors": [
    {
      "message": "Credenciales inválidas",
      "extensions": {
        "classification": "UNAUTHORIZED"
      }
    }
  ]
}
```

```json
{
  "errors": [
    {
      "message": "Usuario no encontrado",
      "extensions": {
        "classification": "NOT_FOUND"
      }
    }
  ]
}
```

```json
{
  "errors": [
    {
      "message": "Usuario inactivo",
      "extensions": {
        "classification": "FORBIDDEN"
      }
    }
  ]
}
```

---

## 📦 Tipos de Datos

### AuthPayload
Respuesta de las operaciones de autenticación (login y register).

```graphql
type AuthPayload {
  token: String!
  type: String!
  usuario: Usuario!
}
```

#### Campos de AuthPayload

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `token` | `String!` | Token JWT para autenticación |
| `type` | `String!` | Tipo de token (siempre "Bearer") |
| `usuario` | `Usuario!` | Información del usuario autenticado |

### RegisterInput
Datos necesarios para registrar un nuevo usuario.

```graphql
input RegisterInput {
  email: String!
  password: String!
  nombre: String!
  apellido: String!
  telefono: String
  sexo: String
}
```

#### Validaciones de RegisterInput

| Campo | Requerido | Validación |
|-------|-----------|------------|
| `email` | ✅ Sí | Formato de email válido, único en sistema |
| `password` | ✅ Sí | Mínimo 6 caracteres (recomendado 8+) |
| `nombre` | ✅ Sí | No vacío |
| `apellido` | ✅ Sí | No vacío |
| `telefono` | ❌ No | Formato recomendado: +591 XXXXXXXX |
| `sexo` | ❌ No | Valores: "Masculino", "Femenino", "Otro" |

### LoginInput
Credenciales para iniciar sesión.

```graphql
input LoginInput {
  email: String!
  password: String!
}
```

---

## 🛠️ Guía de Implementación

### Frontend - Angular Service

#### 1. Crear Auth Service
```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';

const REGISTER_MUTATION = gql`
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
        isActive
      }
    }
  }
`;

const LOGIN_MUTATION = gql`
  mutation Login($input: LoginInput!) {
    login(input: $input) {
      token
      type
      usuario {
        id
        email
        nombre
        apellido
        telefono
        isAdmin
        isAgente
        isCliente
        isActive
      }
    }
  }
`;

const ME_QUERY = gql`
  query Me {
    me {
      id
      email
      nombre
      apellido
      isAdmin
      isAgente
      isCliente
      isActive
    }
  }
`;

export interface AuthResponse {
  token: string;
  type: string;
  usuario: Usuario;
}

export interface Usuario {
  id: string;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  isAdmin: boolean;
  isAgente: boolean;
  isCliente: boolean;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apollo: Apollo) {
    this.loadUserFromToken();
  }

  register(input: {
    email: string;
    password: string;
    nombre: string;
    apellido: string;
    telefono?: string;
    sexo?: string;
  }): Observable<AuthResponse> {
    return this.apollo.mutate<{ register: AuthResponse }>({
      mutation: REGISTER_MUTATION,
      variables: { input }
    }).pipe(
      map(result => result.data!.register),
      tap(response => this.handleAuthResponse(response))
    );
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.apollo.mutate<{ login: AuthResponse }>({
      mutation: LOGIN_MUTATION,
      variables: {
        input: { email, password }
      }
    }).pipe(
      map(result => result.data!.login),
      tap(response => this.handleAuthResponse(response))
    );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    this.currentUserSubject.next(null);
    this.apollo.client.clearStore();
  }

  getCurrentUser(): Observable<Usuario | null> {
    return this.apollo.query<{ me: Usuario }>({
      query: ME_QUERY
    }).pipe(
      map(result => result.data.me),
      tap(user => this.currentUserSubject.next(user))
    );
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem('auth_token', response.token);
    this.currentUserSubject.next(response.usuario);
  }

  private loadUserFromToken(): void {
    if (this.isAuthenticated()) {
      this.getCurrentUser().subscribe();
    }
  }
}
```

#### 2. Configurar Apollo con Interceptor
```typescript
// graphql.module.ts
import { NgModule } from '@angular/core';
import { ApolloModule, APOLLO_OPTIONS } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache, ApolloClientOptions } from '@apollo/client/core';
import { setContext } from '@apollo/client/link/context';

const uri = 'http://localhost:8080/graphql';

export function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  const basic = httpLink.create({ uri });

  const auth = setContext((operation, context) => {
    const token = localStorage.getItem('auth_token');

    if (!token) {
      return {};
    }

    return {
      headers: {
        Authorization: `Bearer ${token}`
      }
    };
  });

  return {
    link: auth.concat(basic),
    cache: new InMemoryCache(),
  };
}

@NgModule({
  exports: [ApolloModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
```

#### 3. Auth Guard
```typescript
// auth.guard.ts
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
```

#### 4. Login Component
```typescript
// login.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error en login:', error);
        this.errorMessage = error.message || 'Credenciales inválidas';
        this.loading = false;
      }
    });
  }
}
```

#### 5. Register Component
```typescript
// register.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      telefono: [''],
      sexo: ['']
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { confirmPassword, ...registerData } = this.registerForm.value;

    this.authService.register(registerData).subscribe({
      next: (response) => {
        console.log('Registro exitoso:', response);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        this.errorMessage = error.message || 'Error al registrar usuario';
        this.loading = false;
      }
    });
  }
}
```

---

## 🔒 Seguridad y Mejores Prácticas

### 🔑 Token JWT

#### Estructura del Token
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.PAYLOAD.SIGNATURE
```

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload (ejemplo):**
```json
{
  "sub": "673ebe1a9f2e3c4d5a6b7c8d",
  "email": "juan.perez@example.com",
  "iat": 1730500000,
  "exp": 1730586400
}
```

#### Tiempo de Expiración
- **Duración típica:** 24 horas (86400 segundos)
- **Recomendación:** Implementar refresh tokens para sesiones prolongadas

### 🛡️ Validaciones de Contraseña

#### Requisitos Mínimos
- ✅ Mínimo 6 caracteres (recomendado 8+)
- ✅ Encriptación con BCrypt
- 🔄 Recomendado: Al menos una mayúscula, una minúscula, un número

#### Ejemplo de Validación en Frontend
```typescript
export class PasswordValidator {
  static strong(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    
    if (!value) {
      return null;
    }
    
    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumeric = /[0-9]/.test(value);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    const isLengthValid = value.length >= 8;
    
    const passwordValid = hasUpperCase && hasLowerCase && hasNumeric && isLengthValid;
    
    return !passwordValid ? {
      passwordStrength: {
        hasUpperCase,
        hasLowerCase,
        hasNumeric,
        hasSpecial,
        isLengthValid
      }
    } : null;
  }
}
```

### 🔐 Almacenamiento Seguro del Token

#### ✅ Recomendado: LocalStorage (para SPA)
```typescript
localStorage.setItem('auth_token', token);
```

#### ⚠️ Alternativa: SessionStorage (sesión temporal)
```typescript
sessionStorage.setItem('auth_token', token);
```

#### ❌ Evitar: Cookies sin HttpOnly (vulnerable a XSS)

### 🚫 Manejo de Sesiones

#### Logout Completo
```typescript
logout(): void {
  // Eliminar token
  localStorage.removeItem('auth_token');
  
  // Limpiar cache de Apollo
  this.apollo.client.clearStore();
  
  // Limpiar estado del usuario
  this.currentUserSubject.next(null);
  
  // Redirigir
  this.router.navigate(['/login']);
}
```

### 🔄 Auto-Login y Persistencia

#### Verificar token al cargar la app
```typescript
// app.component.ts
ngOnInit() {
  if (this.authService.isAuthenticated()) {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        console.log('Usuario cargado:', user);
      },
      error: (error) => {
        // Token inválido o expirado
        this.authService.logout();
      }
    });
  }
}
```

---

## 📊 Flujo de Autenticación Completo

```
┌──────────────┐
│   Usuario    │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│  Login/Register  │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Backend API     │
│  (GraphQL)       │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Validar         │
│  Credenciales    │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Generar JWT     │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Guardar Token   │
│  (LocalStorage)  │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Agregar Header  │
│  Authorization   │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Peticiones      │
│  Autenticadas    │
└──────────────────┘
```

---

## ⚠️ Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `Email ya registrado` | El email ya existe en BD | Usar otro email o intentar login |
| `Credenciales inválidas` | Email o contraseña incorrectos | Verificar datos ingresados |
| `Token expired` | El JWT ha expirado | Realizar login nuevamente |
| `Usuario inactivo` | Cuenta desactivada | Contactar administrador |
| `Unauthorized` | Token no válido o no enviado | Incluir header Authorization |

---

## 🎯 Casos de Uso Avanzados

### 1. Registro con Roles Específicos
Para crear agentes (requiere permisos de admin):
```graphql
mutation {
  createAgente(input: {
    email: "maria.lopez@agencia.com"
    password: "SecurePass456!"
    nombre: "María"
    apellido: "López"
    telefono: "+591 71234567"
    sexo: "Femenino"
    puesto: "Agente de Ventas Senior"
    fechaContratacion: "2025-01-15"
  }) {
    id
    usuarioId
    puesto
    usuario {
      email
      nombre
      isAgente
    }
  }
}
```

### 2. Verificar múltiples roles
```typescript
hasAnyRole(roles: string[]): Observable<boolean> {
  return this.currentUser$.pipe(
    map(user => {
      if (!user) return false;
      return roles.some(role => {
        if (role === 'admin') return user.isAdmin;
        if (role === 'agente') return user.isAgente;
        if (role === 'cliente') return user.isCliente;
        return false;
      });
    })
  );
}
```

### 3. Refresh automático de token (conceptual)
```typescript
// Implementar interceptor HTTP que detecte error 401
// y refresque el token automáticamente
```

---

**Última actualización:** 2025-11-02
**Versión:** 1.0.0
