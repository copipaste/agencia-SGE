# Etapa 1: Build de Angular
FROM node:20-alpine AS build
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar el resto del código
COPY . .

# Build de producción
RUN npm run build -- --configuration production

# Etapa 2: Servidor NGINX
FROM nginx:alpine

# Copiar archivos compilados de Angular
COPY --from=build /app/dist/agencia-frontend/browser /usr/share/nginx/html

# Copiar configuración de NGINX
COPY nginx.conf /etc/nginx/nginx.conf

# Exponer puerto 80
EXPOSE 80

# Comando de inicio
CMD ["nginx", "-g", "daemon off;"]
