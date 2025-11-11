# 📋 Estándares de Codificación del Proyecto

## 🎯 Resumen Ejecutivo

Este documento describe los estándares y convenciones de codificación seguidos en el proyecto **Agencia de Viajes**, que incluye:

- **Backend**: Java 21 + Spring Boot 3.5.7 + GraphQL + MongoDB
- **Frontend**: Angular 19+ + TypeScript + Apollo Client

---

## 🔷 BACKEND (Java/Spring Boot)

### **1. Versiones y Tecnologías**

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| **Java** | 21 | Lenguaje base |
| **Spring Boot** | 3.5.7 | Framework principal |
| **Spring Data MongoDB** | 3.5.7 | Acceso a datos |
| **Spring GraphQL** | 3.5.7 | API GraphQL |
| **Spring Security** | 3.5.7 | Autenticación/Autorización |
| **JWT (jjwt)** | 0.12.6 | Tokens de autenticación |
| **Lombok** | Latest | Reducción de boilerplate |
| **Jakarta Validation** | 3.5.7 | Validación de datos |
| **Maven** | Latest | Gestión de dependencias |

---

### **2. Estructura de Paquetes**

```
com.agencia.agencia_backend/
├── config/          # Configuraciones (DataSeeder, SecurityConfig, etc.)
├── controller/      # Controladores REST (BiController, AuthController, etc.)
├── dto/            # Data Transfer Objects
│   ├── bi/         # DTOs para Business Intelligence
│   └── rest/       # DTOs para API REST
├── graphql/        # Resolvers GraphQL
├── model/          # Entidades de dominio (JPA/MongoDB)
├── repository/     # Repositorios Spring Data
├── security/       # Configuración de seguridad (JWT, Filters)
└── service/        # Lógica de negocio
```

**Convención:**
- ✅ Paquetes en **minúsculas**
- ✅ Separación por **capas** (model, service, repository)
- ✅ Separación por **responsabilidad** (graphql, controller, security)

---

### **3. Convenciones de Nomenclatura**

#### **Clases y Archivos**

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| **Clases** | PascalCase | `AgenteService.java` |
| **Interfaces** | PascalCase | `AgenteRepository.java` |
| **Enums** | PascalCase | `TipoUsuario.java` |
| **Constantes** | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| **Archivos** | PascalCase (igual que clase) | `AgenteService.java` |

#### **Métodos y Variables**

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| **Métodos** | camelCase | `getAgenteById()` |
| **Variables** | camelCase | `agenteId` |
| **Parámetros** | camelCase | `String id` |
| **Campos privados** | camelCase | `private String email` |

#### **Paquetes**

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| **Paquetes** | lowercase | `com.agencia.agencia_backend.service` |
| **Subpaquetes** | lowercase | `com.agencia.agencia_backend.dto.rest` |

---

### **4. Anotaciones y Decoradores**

#### **Anotaciones de Spring**

```java
@Service          // Para servicios
@Repository       // Para repositorios
@Controller       // Para resolvers GraphQL
@RestController   // Para controladores REST
@Configuration    // Para clases de configuración
@Component        // Para componentes genéricos
```

#### **Anotaciones de Lombok**

```java
@Data            // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los parámetros
@RequiredArgsConstructor // Constructor con campos finales
```

#### **Anotaciones de Validación (Jakarta)**

```java
@NotNull         // No puede ser null
@NotBlank        // No puede ser null, vacío o solo espacios
@Email           // Debe ser un email válido
@Size(min=6)     // Tamaño mínimo
@Valid           // Validar objeto anidado
```

#### **Anotaciones de Spring Security**

```java
@PreAuthorize("hasRole('ADMIN')")  // Autorización por roles
@EnableMethodSecurity              // Habilitar seguridad por métodos
```

#### **Anotaciones de GraphQL**

```java
@QueryMapping     // Método de query GraphQL
@MutationMapping  // Método de mutación GraphQL
@SchemaMapping    // Resolver de campo
@Argument         // Parámetro de GraphQL
```

---

### **5. Estructura de Clases**

#### **Modelos (Entidades)**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "agentes")
public class Agente {
    
    @Id
    private String id;
    
    @NotNull(message = "El usuario es obligatorio")
    private String usuarioId;
    
    @NotBlank(message = "El puesto es obligatorio")
    private String puesto;
    
    private LocalDate fechaContratacion;
}
```

**Convenciones:**
- ✅ Usar `@Data` de Lombok
- ✅ Usar `@Document` para MongoDB
- ✅ Validaciones en campos
- ✅ Mensajes descriptivos en validaciones

#### **Repositorios**

```java
@Repository
public interface AgenteRepository extends MongoRepository<Agente, String> {
    
    Optional<Agente> findByUsuarioId(String usuarioId);
}
```

**Convenciones:**
- ✅ Extender `MongoRepository<Entity, ID>`
- ✅ Métodos con prefijos: `findBy`, `existsBy`, `countBy`
- ✅ Retornar `Optional<T>` para búsquedas únicas

#### **Servicios**

```java
@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    /**
     * Obtener agente por ID
     */
    public Optional<Agente> getAgenteById(String id) {
        return agenteRepository.findById(id);
    }
}
```

**Convenciones:**
- ✅ Usar `@Service`
- ✅ Inyección con `@Autowired`
- ✅ JavaDoc para métodos públicos
- ✅ Retornar `Optional<T>` cuando puede ser null
- ✅ Lanzar `RuntimeException` con mensajes descriptivos

#### **Resolvers GraphQL**

```java
@Controller
public class AgenteResolver {

    @Autowired
    private AgenteService agenteService;

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Agente getAgenteById(@Argument String id) {
        return agenteService.getAgenteById(id).orElse(null);
    }
}
```

**Convenciones:**
- ✅ Usar `@Controller` (no `@RestController`)
- ✅ `@QueryMapping` para queries
- ✅ `@MutationMapping` para mutaciones
- ✅ `@PreAuthorize` para seguridad
- ✅ Retornar `null` si no se encuentra (GraphQL maneja null)

#### **DTOs (Data Transfer Objects)**

```java
public class CreateAgenteInput {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
```

**Convenciones:**
- ✅ Sin anotaciones de Lombok (generar getters/setters manualmente si es necesario)
- ✅ Validaciones en campos
- ✅ Mensajes descriptivos

---

### **6. Manejo de Errores**

```java
// Retornar Optional si puede no existir
public Optional<Agente> getAgenteById(String id) {
    return agenteRepository.findById(id);
}

// Lanzar excepción si es obligatorio
public Agente updateAgente(String id, UpdateAgenteInput input) {
    Agente agente = agenteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Agente no encontrado"));
    // ...
}

// Validaciones con mensajes claros
if (usuarioRepository.existsByEmail(input.getEmail())) {
    throw new RuntimeException("Error: El email ya está registrado!");
}
```

**Convenciones:**
- ✅ Usar `Optional<T>` cuando puede ser null
- ✅ Lanzar `RuntimeException` con mensajes descriptivos
- ✅ Validar antes de operar

---

### **7. Formato de Código**

#### **Indentación**
- ✅ **4 espacios** (no tabs)
- ✅ Configurado en `pom.xml`: `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`

#### **Líneas en blanco**
```java
@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    /**
     * Método con JavaDoc
     */
    public List<Agente> getAllAgentes() {
        return agenteRepository.findAll();
    }
}
```

#### **Imports**
- ✅ Organizados automáticamente por IDE
- ✅ Sin imports innecesarios

---

### **8. JavaDoc**

```java
/**
 * Obtener agente por ID
 */
public Optional<Agente> getAgenteById(String id) {
    return agenteRepository.findById(id);
}

/**
 * Crear nuevo agente (crea también el usuario)
 */
public Agente createAgente(CreateAgenteInput input) {
    // ...
}
```

**Convenciones:**
- ✅ JavaDoc en métodos públicos
- ✅ Descripción breve y clara
- ✅ Sin JavaDoc excesivo en métodos privados

---

### **9. Seguridad**

```java
@QueryMapping
@PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
public Agente getAgenteById(@Argument String id) {
    return agenteService.getAgenteById(id).orElse(null);
}
```

**Convenciones:**
- ✅ `@PreAuthorize` en todos los endpoints
- ✅ Roles en mayúsculas: `'ADMIN'`, `'AGENTE'`, `'CLIENTE'`
- ✅ JWT en headers: `Authorization: Bearer <token>`

---

## 🔶 FRONTEND (Angular/TypeScript)

### **1. Versiones y Tecnologías**

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| **Angular** | 19+ | Framework principal |
| **TypeScript** | 5.x | Lenguaje base |
| **Apollo Angular** | Latest | Cliente GraphQL |
| **RxJS** | Latest | Programación reactiva |
| **Reactive Forms** | Latest | Formularios |

---

### **2. Configuración TypeScript**

```json
{
  "compilerOptions": {
    "strict": true,                    // Modo estricto habilitado
    "noImplicitOverride": true,        // Requerir override explícito
    "noPropertyAccessFromIndexSignature": true,
    "noImplicitReturns": true,         // Requerir return explícito
    "noFallthroughCasesInSwitch": true,
    "target": "ES2022",                // Target ES2022
    "module": "ES2022"
  },
  "angularCompilerOptions": {
    "strictInjectionParameters": true,
    "strictInputAccessModifiers": true,
    "strictTemplates": true            // Templates estrictos
  }
}
```

**Características:**
- ✅ **Modo estricto** habilitado
- ✅ **Target ES2022**
- ✅ **Templates estrictos**

---

### **3. Estructura de Carpetas**

```
src/app/
├── components/        # Componentes reutilizables
│   ├── confirm-dialog/
│   ├── export-buttons/
│   └── filter-panel/
├── guards/           # Guards de routing
│   └── auth.guard.ts
├── models/           # Interfaces y tipos TypeScript
│   ├── agente.model.ts
│   ├── cliente.model.ts
│   └── user.model.ts
├── pages/            # Páginas/Componentes de páginas
│   ├── agentes/
│   │   ├── agente-form/
│   │   ├── agente-list/
│   │   └── agente-show/
│   └── clientes/
├── services/         # Servicios (API, lógica de negocio)
│   ├── agente.service.ts
│   ├── auth.service.ts
│   └── cliente.service.ts
├── app.component.ts
├── app.config.ts
├── app.routes.ts
└── graphql.module.ts
```

**Convenciones:**
- ✅ Carpetas en **minúsculas**
- ✅ Componentes agrupados por **feature** (agentes, clientes, etc.)
- ✅ Separación por **responsabilidad** (services, models, guards)

---

### **4. Convenciones de Nomenclatura**

#### **Archivos y Clases**

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| **Componentes** | kebab-case | `agente-list.component.ts` |
| **Servicios** | kebab-case | `agente.service.ts` |
| **Models** | kebab-case | `agente.model.ts` |
| **Guards** | kebab-case | `auth.guard.ts` |
| **Clases** | PascalCase | `AgenteListComponent` |
| **Interfaces** | PascalCase | `Agente`, `CreateAgenteInput` |

#### **Variables y Métodos**

| Tipo | Convención | Ejemplo |
|------|-----------|---------|
| **Variables** | camelCase | `agentes: Agente[]` |
| **Métodos** | camelCase | `loadAgentes()` |
| **Constantes** | UPPER_SNAKE_CASE | `MAX_ITEMS_PER_PAGE` |
| **Props de componente** | camelCase | `@Input() agenteId: string` |

#### **Selectores de Componentes**

```typescript
@Component({
  selector: 'app-agente-list',  // Prefijo 'app-'
  // ...
})
```

**Convenciones:**
- ✅ Prefijo `app-` para todos los componentes
- ✅ kebab-case en selectores

---

### **5. Estructura de Componentes**

#### **Componentes Standalone**

```typescript
@Component({
  selector: 'app-agente-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './agente-list.component.html',
  styleUrls: ['./agente-list.component.css']
})
export class AgenteListComponent implements OnInit {
  agentes: Agente[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private agenteService: AgenteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAgentes();
  }

  loadAgentes(): void {
    this.loading = true;
    this.agenteService.getAllAgentes().subscribe({
      next: (agentes) => {
        this.agentes = agentes;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar agentes:', error);
        this.errorMessage = 'Error al cargar la lista de agentes';
        this.loading = false;
      }
    });
  }
}
```

**Convenciones:**
- ✅ **Standalone components** (no módulos)
- ✅ `implements OnInit` para lifecycle hooks
- ✅ Propiedades públicas para template
- ✅ Inyección en constructor
- ✅ Manejo de errores con `subscribe({ next, error })`

---

### **6. Servicios**

```typescript
@Injectable({
  providedIn: 'root'
})
export class AgenteService {
  constructor(private apollo: Apollo) {}

  getAllAgentes(): Observable<Agente[]> {
    return this.apollo.query<{ getAllAgentes: Agente[] }>({
      query: GET_ALL_AGENTES,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getAllAgentes || [])
    );
  }

  getAgenteById(id: string): Observable<Agente> {
    return this.apollo.query<{ getAgenteById: Agente }>({
      query: GET_AGENTE_BY_ID,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data!.getAgenteById)
    );
  }
}
```

**Convenciones:**
- ✅ `@Injectable({ providedIn: 'root' })`
- ✅ Retornar `Observable<T>`
- ✅ Usar `apollo.query()` para queries (no `watchQuery()`)
- ✅ `fetchPolicy: 'network-only'` para datos frescos
- ✅ Usar `pipe(map())` para transformar datos
- ✅ Operador `!` solo cuando se sabe que existe

---

### **7. Models (Interfaces)**

```typescript
export interface Agente {
  id: string;
  usuarioId: string;
  puesto: string;
  fechaContratacion?: string;
  usuario?: {
    id: string;
    email: string;
    nombre: string;
    apellido: string;
    telefono?: string;
    sexo?: string;
    isActive?: boolean;
  };
}

export interface CreateAgenteInput {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  sexo?: string;
  puesto: string;
  fechaContratacion?: string;
}

export interface UpdateAgenteInput {
  puesto?: string;
  fechaContratacion?: string;
}
```

**Convenciones:**
- ✅ Usar `interface` (no `class`)
- ✅ Propiedades opcionales con `?`
- ✅ Nombres descriptivos
- ✅ Separar Input/Output types

---

### **8. GraphQL Queries/Mutations**

```typescript
const GET_ALL_AGENTES = gql`
  query GetAllAgentes {
    getAllAgentes {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isActive
      }
    }
  }
`;

const GET_AGENTE_BY_ID = gql`
  query GetAgenteById($id: ID!) {
    getAgenteById(id: $id) {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isActive
      }
    }
  }
`;
```

**Convenciones:**
- ✅ Queries en **MAYÚSCULAS** con `const`
- ✅ Nombres descriptivos: `GET_ALL_AGENTES`, `GET_AGENTE_BY_ID`
- ✅ Variables tipadas: `$id: ID!`
- ✅ Campos solicitados explícitamente
- ✅ Fragmentos si se reutilizan campos

---

### **9. Manejo de Errores**

```typescript
this.agenteService.getAllAgentes().subscribe({
  next: (agentes) => {
    this.agentes = agentes;
    this.loading = false;
  },
  error: (error) => {
    console.error('Error al cargar agentes:', error);
    this.errorMessage = 'Error al cargar la lista de agentes';
    this.loading = false;
  }
});
```

**Convenciones:**
- ✅ Usar `subscribe({ next, error })`
- ✅ `console.error()` para logs de error
- ✅ Mensajes de error descriptivos para usuario
- ✅ Siempre limpiar `loading = false` en error

---

### **10. Reactive Forms**

```typescript
this.agenteForm = this.fb.group({
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(6)]],
  nombre: ['', Validators.required],
  apellido: ['', Validators.required],
  telefono: [''],
  sexo: [''],
  puesto: ['', Validators.required],
  fechaContratacion: ['']
});
```

**Convenciones:**
- ✅ Usar `FormBuilder` (`fb`)
- ✅ Validadores en array
- ✅ Validaciones: `required`, `email`, `minLength`
- ✅ Valores por defecto: `['']` o `['valor']`

---

### **11. Formato de Código**

#### **Indentación**
- ✅ **2 espacios** (estándar de Angular)

#### **Líneas en blanco**
```typescript
export class AgenteListComponent implements OnInit {
  agentes: Agente[] = [];
  loading = false;

  constructor(
    private agenteService: AgenteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAgentes();
  }
}
```

#### **Imports**
```typescript
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AgenteService } from '../../../services/agente.service';
import { Agente } from '../../../models/agente.model';
```

**Convenciones:**
- ✅ Imports de Angular primero
- ✅ Imports de terceros después
- ✅ Imports locales al final
- ✅ Agrupar por tipo

---

### **12. Rutas**

```typescript
export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'agentes', component: AgenteListComponent },
      { path: 'agentes/ver/:id', component: AgenteShowComponent },
      { path: 'agentes/editar/:id', component: AgenteFormComponent },
      { path: 'agentes/nuevo', component: AgenteFormComponent }
    ]
  }
];
```

**Convenciones:**
- ✅ Rutas en **kebab-case**
- ✅ Parámetros con `:id`
- ✅ Guards para rutas protegidas
- ✅ Rutas anidadas con `children`

---

## 📊 Resumen de Convenciones

### **Backend (Java)**

| Aspecto | Convención |
|---------|-----------|
| **Indentación** | 4 espacios |
| **Naming** | PascalCase (clases), camelCase (métodos/variables) |
| **Paquetes** | lowercase |
| **Anotaciones** | Lombok (@Data), Spring (@Service, @Repository) |
| **Validaciones** | Jakarta Validation (@NotNull, @NotBlank) |
| **Errores** | RuntimeException con mensajes descriptivos |
| **JavaDoc** | En métodos públicos |

### **Frontend (TypeScript)**

| Aspecto | Convención |
|---------|-----------|
| **Indentación** | 2 espacios |
| **Naming** | PascalCase (clases), camelCase (variables/métodos), kebab-case (archivos) |
| **Carpetas** | lowercase |
| **Componentes** | Standalone components |
| **Servicios** | `providedIn: 'root'` |
| **GraphQL** | `apollo.query()` (no `watchQuery()`) |
| **Errores** | `subscribe({ next, error })` |
| **TypeScript** | Modo estricto habilitado |

---

## 🔍 Herramientas y Configuraciones

### **Backend**
- ✅ **Maven** para gestión de dependencias
- ✅ **Lombok** para reducir boilerplate
- ✅ **UTF-8** encoding en `pom.xml`
- ✅ **Java 21** como versión objetivo

### **Frontend**
- ✅ **Angular CLI** para generación de código
- ✅ **TypeScript strict mode** habilitado
- ✅ **ESLint** (si está configurado)
- ✅ **Prettier** (si está configurado)

---

## ✅ Checklist de Cumplimiento

### **Al escribir código nuevo:**

#### **Backend:**
- [ ] Usar Lombok (`@Data`, `@NoArgsConstructor`)
- [ ] Validaciones con Jakarta Validation
- [ ] JavaDoc en métodos públicos
- [ ] `@PreAuthorize` en endpoints
- [ ] Retornar `Optional<T>` cuando puede ser null
- [ ] Mensajes de error descriptivos

#### **Frontend:**
- [ ] Componentes standalone
- [ ] Tipos TypeScript explícitos
- [ ] Manejo de errores con `subscribe({ next, error })`
- [ ] `fetchPolicy: 'network-only'` en queries
- [ ] Validaciones en formularios
- [ ] Nombres descriptivos en variables/métodos

---

## 📚 Referencias

- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Angular Style Guide](https://angular.io/guide/styleguide)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/handbook/intro.html)
- [GraphQL Best Practices](https://graphql.org/learn/best-practices/)

---

**Última actualización:** Noviembre 1, 2025  
**Versión del documento:** 1.0

