# 📋 Instrucciones de Instalación y Ejecución del Proyecto

## 🗂️ Estructura del Proyecto

```
EXAMEN1/
├── agencia-backend/     # Servidor Spring Boot + GraphQL
└── agencia-frontend/    # Cliente Angular
```

---

## 🔧 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

### Backend:
- ☕ **Java JDK 21** o superior
- 📦 **Maven 3.8+** (incluido en el proyecto como `mvnw`)

### Frontend:
- 🟢 **Node.js 18+** o superior
- 📦 **npm** (viene con Node.js)
- 🅰️ **Angular CLI** (se instalará automáticamente)

### Base de Datos:
- 🍃 **MongoDB Atlas** (cuenta gratuita) o MongoDB local

---

## 📊 Parte 1: Configuración de la Base de Datos MongoDB

### Opción A: MongoDB Atlas (Recomendado - Nube)

1. **Crear cuenta en MongoDB Atlas:**
   - Ve a https://www.mongodb.com/cloud/atlas
   - Crea una cuenta gratuita

2. **Crear un Cluster:**
   - Selecciona "Create a New Cluster"
   - Elige el plan gratuito (M0)
   - Selecciona tu región más cercana
   - Click en "Create Cluster"

3. **Configurar acceso:**
   - Ve a "Database Access" → "Add New Database User"
   - Crea un usuario con contraseña (guarda estos datos)
   - Ve a "Network Access" → "Add IP Address"
   - Selecciona "Allow Access from Anywhere" (0.0.0.0/0) o agrega tu IP específica

4. **Obtener la URL de conexión:**
   - Click en "Connect" en tu cluster
   - Selecciona "Connect your application"
   - Copia la cadena de conexión (MongoDB URI)
   - Debería verse así: `mongodb+srv://<usuario>:<password>@cluster0.xxxxx.mongodb.net/<database>?retryWrites=true&w=majority`

### Opción B: MongoDB Local

1. **Instalar MongoDB Community Edition:**
   - Descarga desde: https://www.mongodb.com/try/download/community
   - Instala y sigue las instrucciones del instalador

2. **URL de conexión local:**
   ```
   mongodb://localhost:27017/agencia_viajes
   ```

---

## ⚙️ Parte 2: Configuración del Backend (Spring Boot)

### 📍 Ubicación del Archivo de Configuración

**Archivo:** `agencia-backend/src/main/resources/application.properties`

### 🔐 Configurar la URL de MongoDB

Abre el archivo `application.properties` y localiza la línea:

```properties
spring.data.mongodb.uri=mongodb+srv://usuario:password@cluster0.xxxxx.mongodb.net/agencia_viajes?retryWrites=true&w=majority
```

**Reemplaza con tu URL de MongoDB:**

#### Para MongoDB Atlas:
```properties
spring.data.mongodb.uri=mongodb+srv://<TU_USUARIO>:<TU_PASSWORD>@<TU_CLUSTER>.mongodb.net/agencia_viajes?retryWrites=true&w=majority
```

#### Para MongoDB Local:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/agencia_viajes
```

**⚠️ Importante:** 
- Reemplaza `<TU_USUARIO>`, `<TU_PASSWORD>` y `<TU_CLUSTER>` con tus datos reales
- Si la contraseña contiene caracteres especiales, codifícalos en URL (por ejemplo: `@` = `%40`)

### 🚀 Ejecutar el Backend

1. **Abrir terminal en la carpeta del backend:**
   ```bash
   cd agencia-backend
   ```

2. **Compilar el proyecto (opcional pero recomendado):**
   
   **Windows (PowerShell/CMD):**
   ```bash
   .\mvnw.cmd clean install
   ```
   

3. **Ejecutar el servidor:**
   
   **Windows (PowerShell/CMD):**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   

4. **Verificar que el backend esté corriendo:**
   - El servidor estará disponible en: `http://localhost:8080`
   - GraphQL Playground: `http://localhost:8080/graphiql`
   - Deberías ver en la consola: `Started AgenciaBackendApplication`

---

## 🎨 Parte 3: Configuración del Frontend (Angular)

### 📍 Configurar la URL del Backend

**Archivo:** `agencia-frontend/src/app/graphql.module.ts`

Abre el archivo y localiza la línea:

```typescript
const uri = 'http://localhost:8080/graphql';
```

**Cambiar según tu entorno:**

#### Backend en la misma máquina (desarrollo local):
```typescript
const uri = 'http://localhost:8080/graphql';
```

#### Backend en otra máquina de la red local:
```typescript
const uri = 'http://192.168.1.100:8080/graphql';  // Reemplaza con la IP del servidor
```

#### Backend en servidor remoto:
```typescript
const uri = 'http://tu-servidor.com:8080/graphql';  // Reemplaza con tu dominio o IP pública
```

### 🚀 Ejecutar el Frontend

1. **Abrir terminal en la carpeta del frontend:**
   ```bash
   cd agencia-frontend
   ```

2. **Instalar dependencias (solo la primera vez):**
   ```bash
   npm install
   ```
   
   ⏱️ Este proceso puede tardar varios minutos

3. **Ejecutar el servidor de desarrollo:**
   ```bash
   ng serve
   ```
   
   O con npm:
   ```bash
   npm start
   ```

4. **Acceder a la aplicación:**
   - Abre tu navegador en: `http://localhost:4200`
   - La aplicación se recargará automáticamente cuando hagas cambios en el código

---

## 🔄 Orden de Ejecución Recomendado

### Para desarrollo:

1. **Primero:** Iniciar MongoDB (si es local) o verificar conexión a Atlas
2. **Segundo:** Iniciar el Backend (Spring Boot)
3. **Tercero:** Iniciar el Frontend (Angular)

### Comandos rápidos:

```bash
# Terminal 1 - Backend
cd agencia-backend
.\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend (en otra ventana)
cd agencia-frontend
ng serve
```

---

## 🧪 Verificar que Todo Funciona

### 1. Backend:
- ✅ Accede a `http://localhost:8080/graphiql`
- ✅ Deberías ver la interfaz de GraphQL Playground
- ✅ Prueba una query simple:
  ```graphql
    query {
    hello
    }
  ```

### 2. Frontend:
- ✅ Accede a `http://localhost:4200`
- ✅ Deberías ver la página de login
- ✅ Intenta iniciar sesión o registrarte

### 3. Conexión MongoDB:
- ✅ Si el backend inicia sin errores, MongoDB está conectado correctamente
- ✅ Verifica en los logs del backend: `"Connected to MongoDB"`

---

## 🔐 Usuarios de Prueba

Para acceder a la aplicación, puedes usar estos usuarios de prueba:

### Administrador:
- **Email:** admin@agencia.com
- **Password:** admin123

### Cliente:
- **Email:** cliente@test.com
- **Password:** cliente123

### Agente:
- **Email:** agente@agencia.com
- **Password:** agente123

*(Estos usuarios se crean automaticamente en la base de datos)*

---

## 📝 Notas Adicionales

### Puertos utilizados:
- **Backend:** `8080`
- **Frontend:** `4200`
- **MongoDB Local:** `27017` (si usas local)

### Si cambias de puerto el backend:

1. **Backend:** Modifica `application.properties`:
   ```properties
   server.port=8080  # Cambia este número
   ```

2. **Frontend:** Actualiza `graphql.module.ts`:
   ```typescript
   const uri = 'http://localhost:8080/graphql';  # Actualiza el puerto
   ```

### CORS (Cross-Origin Resource Sharing):

El backend ya está configurado para aceptar peticiones desde `http://localhost:4200`. Si despliegas en otro dominio, actualiza la configuración de CORS en el backend.

---

## 🐛 Problemas Comunes y Soluciones

### Backend no inicia:

**Problema:** `Could not connect to MongoDB`
- **Solución:** Verifica la URL de MongoDB en `application.properties`
- Verifica que tu IP esté en la whitelist de MongoDB Atlas
- Verifica usuario y contraseña

**Problema:** `Port 8080 already in use`
- **Solución:** Cambia el puerto en `application.properties` o cierra la aplicación que usa el puerto

### Frontend no inicia:

**Problema:** `npm install` falla
- **Solución:** Elimina `node_modules` y `package-lock.json`, luego ejecuta `npm install` de nuevo

**Problema:** `ng: command not found`
- **Solución:** Instala Angular CLI globalmente: `npm install -g @angular/cli`

**Problema:** Frontend no se conecta al backend
- **Solución:** Verifica que el backend esté corriendo y la URL en `graphql.module.ts` sea correcta

### MongoDB:

**Problema:** `Authentication failed`
- **Solución:** Verifica usuario y contraseña en la URL de conexión
- Codifica caracteres especiales en la contraseña

**Problema:** `Network timeout`
- **Solución:** Verifica tu IP en la whitelist de MongoDB Atlas
- Verifica tu conexión a internet

---

## 📦 Compilar para Producción

### Backend:

```bash
cd agencia-backend
.\mvnw.cmd clean package
```

El archivo JAR se generará en: `target/agencia-backend-0.0.1-SNAPSHOT.jar`

Ejecutar el JAR:
```bash
java -jar target/agencia-backend-0.0.1-SNAPSHOT.jar
```

### Frontend:

```bash
cd agencia-frontend
ng build --configuration production
```

Los archivos se generarán en: `dist/agencia-frontend/`

---

## 🆘 Soporte

Si encuentras problemas:

1. Verifica los logs en la consola del backend
2. Abre las herramientas de desarrollo del navegador (F12) para ver errores del frontend
3. Verifica que todos los servicios estén corriendo (MongoDB, Backend, Frontend)
4. Revisa que las URLs de conexión sean correctas

---

## ✅ Checklist de Instalación

- [ ] Java JDK 21+ instalado
- [ ] Node.js 18+ instalado
- [ ] MongoDB configurado (Atlas o local)
- [ ] URL de MongoDB configurada en `application.properties`
- [ ] Backend compilado exitosamente
- [ ] Backend corriendo en `http://localhost:8080`
- [ ] `npm install` ejecutado en el frontend
- [ ] URL del backend configurada en `graphql.module.ts`
- [ ] Frontend corriendo en `http://localhost:4200`
- [ ] Login funciona correctamente
- [ ] Puedes navegar por los módulos (Clientes, Agentes, etc.)

---

## 🎉 ¡Listo!

Si completaste todos los pasos, tu aplicación de Agencia de Viajes debería estar funcionando correctamente. 

**Happy coding! 🚀**
