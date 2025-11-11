# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080
EXPOSE 8080

# Variables de entorno (se configurarán en Azure)
ENV MONGODB_URI=""
ENV JWT_SECRET=""
ENV SERVER_PORT=8080
ENV CORS_ALLOWED_ORIGINS=""

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
