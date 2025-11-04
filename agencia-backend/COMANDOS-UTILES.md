# ⚡ Comandos Útiles - PowerShell

## 🚀 Comandos de Inicio Rápido

### Iniciar MongoDB
```powershell
# Opción 1: Si está instalado como servicio
net start MongoDB

# Opción 2: Ejecutar directamente
mongod

# Opción 3: Con ruta específica de datos
mongod --dbpath C:\data\db
```

### Compilar el Proyecto
```powershell
# Limpiar y compilar
.\mvnw.cmd clean compile

# Limpiar, compilar y empaquetar
.\mvnw.cmd clean package

# Limpiar, compilar y ejecutar tests
.\mvnw.cmd clean test

# Instalar en repositorio local
.\mvnw.cmd clean install
```

### Ejecutar la Aplicación
```powershell
# Ejecutar con Maven
.\mvnw.cmd spring-boot:run

# Ejecutar con hot-reload (DevTools activo)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.fork=false

# Ejecutar en un puerto diferente
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Ejecutar con perfil específico
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Ejecutar JAR Compilado
```powershell
# Compilar JAR
.\mvnw.cmd clean package

# Ejecutar JAR
java -jar target\agencia-backend-0.0.1-SNAPSHOT.jar

# Ejecutar en puerto diferente
java -jar target\agencia-backend-0.0.1-SNAPSHOT.jar --server.port=8081
```

## 🗄️ Comandos de MongoDB

### Conectar a MongoDB
```powershell
# Conectar con mongosh
mongosh

# Conectar a base de datos específica
mongosh mongodb://localhost:27017/agencia_viajes
```

### Comandos dentro de mongosh
```javascript
// Usar base de datos
use agencia_viajes

// Ver colecciones
show collections

// Contar documentos
db.usuarios.countDocuments()

// Ver todos los usuarios
db.usuarios.find().pretty()

// Buscar usuario por email
db.usuarios.findOne({email: "admin@agencia.com"})

// Crear usuario admin
db.usuarios.insertOne({
  email: "admin@agencia.com",
  password: "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
  nombre: "Admin",
  apellido: "Principal",
  telefono: "999999999",
  sexo: "M",
  isAdmin: true,
  isAgente: false,
  isCliente: false,
  isActive: true
})

// Eliminar todos los documentos de una colección
db.usuarios.deleteMany({})

// Eliminar la base de datos
db.dropDatabase()

// Salir
exit
```

### Backup y Restore
```powershell
# Backup de toda la base de datos
mongodump --db agencia_viajes --out C:\backup\mongodb

# Restore
mongorestore --db agencia_viajes C:\backup\mongodb\agencia_viajes

# Backup de una colección específica
mongodump --db agencia_viajes --collection usuarios --out C:\backup\mongodb

# Restore de una colección específica
mongorestore --db agencia_viajes --collection usuarios C:\backup\mongodb\agencia_viajes\usuarios.bson
```

## 🧪 Comandos de Testing

### Testing con cURL (PowerShell)

#### Login
```powershell
$loginBody = @{
    email = "admin@agencia.com"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json"

$token = $loginResponse.token
Write-Host "Token obtenido: $token"
```

#### Registro
```powershell
$registerBody = @{
    email = "nuevo@example.com"
    password = "password123"
    nombre = "Juan"
    apellido = "Pérez"
    telefono = "123456789"
    sexo = "M"
} | ConvertTo-Json

$registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -Body $registerBody `
    -ContentType "application/json"

Write-Host "Usuario registrado con token: $($registerResponse.token)"
```

#### Endpoint Protegido
```powershell
# Primero hacer login y guardar el token
$loginBody = @{
    email = "admin@agencia.com"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json"

$token = $loginResponse.token

# Usar el token para acceder a endpoint protegido
$headers = @{
    Authorization = "Bearer $token"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/test/user" `
    -Method Get `
    -Headers $headers

$response | ConvertTo-Json
```

#### Test Público
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/test/public" -Method Get
```

## 📊 Comandos de Monitoreo

### Ver Procesos Java
```powershell
# Ver todos los procesos Java
Get-Process java

# Ver detalles del proceso Spring Boot
Get-Process java | Where-Object {$_.ProcessName -eq "java"} | Format-List *

# Matar proceso Java (si se quedó colgado)
Stop-Process -Name java -Force
```

### Ver Puerto en Uso
```powershell
# Ver qué está usando el puerto 8080
netstat -ano | findstr :8080

# Ver procesos MongoDB
Get-Process mongod

# Matar proceso en puerto específico
$port = 8080
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
if ($process) {
    Stop-Process -Id $process.OwningProcess -Force
}
```

## 🔧 Comandos de Desarrollo

### Limpiar Cache de Maven
```powershell
# Limpiar todo el cache local de Maven
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository

# Limpiar solo dependencias del proyecto
.\mvnw.cmd dependency:purge-local-repository
```

### Ver Dependencias
```powershell
# Ver árbol de dependencias
.\mvnw.cmd dependency:tree

# Ver dependencias desactualizadas
.\mvnw.cmd versions:display-dependency-updates

# Ver plugins desactualizados
.\mvnw.cmd versions:display-plugin-updates
```

### Actualizar Dependencias
```powershell
# Actualizar versiones de dependencias
.\mvnw.cmd versions:use-latest-releases

# Actualizar solo dependencias SNAPSHOT
.\mvnw.cmd versions:use-latest-snapshots
```

## 📝 Scripts Útiles

### Script de Inicio Completo
```powershell
# inicio.ps1
Write-Host "🚀 Iniciando Agencia de Viajes Backend..." -ForegroundColor Green

# Verificar si MongoDB está corriendo
$mongoProcess = Get-Process mongod -ErrorAction SilentlyContinue
if (!$mongoProcess) {
    Write-Host "⚠️  MongoDB no está corriendo. Iniciando..." -ForegroundColor Yellow
    Start-Process mongod -WindowStyle Hidden
    Start-Sleep -Seconds 3
}

# Compilar y ejecutar
Write-Host "📦 Compilando proyecto..." -ForegroundColor Cyan
.\mvnw.cmd clean package -DskipTests

Write-Host "▶️  Ejecutando aplicación..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run
```

### Script de Testing
```powershell
# test.ps1
Write-Host "🧪 Ejecutando Tests..." -ForegroundColor Cyan

# Ejecutar tests
.\mvnw.cmd test

# Generar reporte de cobertura (si tienes JaCoCo configurado)
.\mvnw.cmd jacoco:report

Write-Host "✅ Tests completados" -ForegroundColor Green
```

### Script de Limpieza
```powershell
# limpiar.ps1
Write-Host "🧹 Limpiando proyecto..." -ForegroundColor Yellow

# Limpiar target
.\mvnw.cmd clean

# Limpiar node_modules si existe (para frontend)
if (Test-Path "node_modules") {
    Remove-Item -Recurse -Force node_modules
}

# Limpiar logs si existen
if (Test-Path "logs") {
    Remove-Item -Recurse -Force logs
}

Write-Host "✅ Proyecto limpio" -ForegroundColor Green
```

## 🔍 Comandos de Debug

### Ver Logs en Tiempo Real
```powershell
# Si guardas logs en archivo
Get-Content logs\application.log -Wait -Tail 50
```

### Ejecutar en Modo Debug
```powershell
# Ejecutar con debug remoto en puerto 5005
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Luego en VS Code, configurar launch.json para conectar al puerto 5005
```

### Variables de Entorno
```powershell
# Establecer variable de entorno temporalmente
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run

# Establecer múltiples variables
$env:SPRING_PROFILES_ACTIVE="dev"
$env:MONGODB_URI="mongodb://localhost:27017/agencia_viajes_dev"
.\mvnw.cmd spring-boot:run
```

## 📦 Comandos de Deployment

### Crear JAR Ejecutable
```powershell
# Crear JAR sin ejecutar tests
.\mvnw.cmd clean package -DskipTests

# El JAR estará en: target\agencia-backend-0.0.1-SNAPSHOT.jar
```

### Crear Docker Image (si tienes Dockerfile)
```powershell
# Build Docker image
docker build -t agencia-backend:latest .

# Ejecutar container
docker run -p 8080:8080 agencia-backend:latest

# Con MongoDB en Docker
docker-compose up -d
```

## 🎯 Comandos Rápidos de Un Solo Uso

```powershell
# Compilar, testear y ejecutar
.\mvnw.cmd clean install && .\mvnw.cmd spring-boot:run

# Ver versión de Java
java -version

# Ver versión de Maven
.\mvnw.cmd -version

# Verificar sintaxis del pom.xml
.\mvnw.cmd validate

# Limpiar y empaquetar sin tests
.\mvnw.cmd clean package -DskipTests -Dmaven.test.skip=true

# Ejecutar un test específico
.\mvnw.cmd test -Dtest=UsuarioServiceTest

# Ver información del proyecto
.\mvnw.cmd help:effective-pom
```

## 💡 Tips

1. **Alias Útiles**: Puedes crear alias en tu perfil de PowerShell:
```powershell
# En $PROFILE (notepad $PROFILE)
function mvnrun { .\mvnw.cmd spring-boot:run }
function mvnclean { .\mvnw.cmd clean install }
function mvntest { .\mvnw.cmd test }
```

2. **Atajos de Teclado en VS Code**:
   - `Ctrl + F5`: Run without debugging
   - `F5`: Run with debugging
   - `Ctrl + C`: Detener ejecución en terminal

3. **Hot Reload**: DevTools está incluido, los cambios en Java se recargan automáticamente

4. **Puerto Ocupado**: Si el puerto 8080 está ocupado, usa:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## 📚 Recursos Adicionales

- **Spring Boot Docs**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **MongoDB Manual**: https://docs.mongodb.com/manual/
- **Spring Security**: https://docs.spring.io/spring-security/reference/
- **JWT**: https://jwt.io/

¡Guarda este archivo para tener todos los comandos a mano! 🚀
