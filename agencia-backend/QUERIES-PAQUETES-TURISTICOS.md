# 📦 Queries y Mutations - Paquetes Turísticos

## 📖 Descripción General
Este documento contiene todas las queries y mutations disponibles para el módulo de **Paquetes Turísticos** en el sistema de gestión de agencia de viajes.

---

## 🔍 QUERIES (Consultas)

### 1️⃣ Obtener todos los paquetes turísticos
```graphql
query {
  getAllPaquetesTuristicos {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      precioVenta
      proveedor {
        nombreEmpresa
      }
    }
  }
}
```
**Descripción**: Retorna la lista completa de todos los paquetes turísticos registrados en el sistema.

**Permisos**: Requiere autenticación (Admin o Agente)

---

### 2️⃣ Obtener paquete turístico por ID
```graphql
query {
  getPaqueteTuristicoById(id: "673cd79d2e9af5567b9c0c20") {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioVenta
      disponibilidad
      proveedor {
        nombreEmpresa
        contactoEmail
      }
    }
  }
}
```
**Descripción**: Retorna un paquete turístico específico basado en su ID, incluyendo todos los servicios que lo componen.

**Parámetros**:
- `id` (ID!): ID único del paquete turístico (MongoDB ObjectId)

**Permisos**: Requiere autenticación

---

### 3️⃣ Obtener paquetes turísticos por destino
```graphql
query {
  getPaquetesTuristicosByDestino(destino: "Uyuni") {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      tipoServicio
      nombreServicio
      destinoCiudad
    }
  }
}
```
**Descripción**: Retorna todos los paquetes turísticos que tienen como destino principal una ciudad específica.

**Parámetros**:
- `destino` (String!): Destino principal a buscar

**Permisos**: Requiere autenticación

---

### 4️⃣ Buscar paquetes turísticos
```graphql
query {
  searchPaquetesTuristicos(keyword: "Salar") {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      nombreServicio
      tipoServicio
    }
  }
}
```
**Descripción**: Busca paquetes turísticos cuyo nombre o descripción contenga la palabra clave.

**Parámetros**:
- `keyword` (String!): Palabra clave a buscar en nombre o descripción

**Permisos**: Requiere autenticación

---

## ✏️ MUTATIONS (Operaciones de Escritura)

### 1️⃣ Crear nuevo paquete turístico
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Paquete Salar de Uyuni Completo"
    descripcion: "Tour completo al Salar de Uyuni con hospedaje, transporte y alimentación incluidos"
    destinoPrincipal: "Uyuni"
    duracionDias: 3
    precioTotalVenta: 2500.00
    serviciosIds: [
      "673cd79d2e9af5567b9c0c10",
      "673cd79d2e9af5567b9c0c11",
      "673cd79d2e9af5567b9c0c12"
    ]
  }) {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      tipoServicio
      nombreServicio
      precioVenta
    }
  }
}
```
**Descripción**: Crea un nuevo paquete turístico en el sistema con los servicios especificados.

**Input**:
- `nombrePaquete` (String!): Nombre descriptivo del paquete
- `descripcion` (String): Descripción detallada del paquete (opcional)
- `destinoPrincipal` (String!): Destino principal del paquete
- `duracionDias` (Int): Duración en días (opcional)
- `precioTotalVenta` (Float): Precio total de venta del paquete (opcional)
- `serviciosIds` ([ID!]): Lista de IDs de servicios incluidos en el paquete (opcional)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El paquete turístico creado con todos sus datos

---

### 2️⃣ Actualizar paquete turístico
```graphql
mutation {
  updatePaqueteTuristico(
    id: "673cd79d2e9af5567b9c0c20"
    input: {
      nombrePaquete: "Paquete Salar de Uyuni Premium"
      descripcion: "Tour premium al Salar de Uyuni con hospedaje 5 estrellas, transporte privado y todas las comidas incluidas"
      duracionDias: 4
      precioTotalVenta: 3500.00
      serviciosIds: [
        "673cd79d2e9af5567b9c0c10",
        "673cd79d2e9af5567b9c0c11",
        "673cd79d2e9af5567b9c0c12",
        "673cd79d2e9af5567b9c0c13"
      ]
    }
  ) {
    id
    nombrePaquete
    descripcion
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      id
      nombreServicio
      tipoServicio
    }
  }
}
```
**Descripción**: Actualiza los datos de un paquete turístico existente.

**Parámetros**:
- `id` (ID!): ID del paquete turístico a actualizar

**Input** (todos opcionales):
- `nombrePaquete` (String): Nuevo nombre del paquete
- `descripcion` (String): Nueva descripción
- `destinoPrincipal` (String): Nuevo destino principal
- `duracionDias` (Int): Nueva duración
- `precioTotalVenta` (Float): Nuevo precio total
- `serviciosIds` ([ID!]): Nueva lista completa de servicios (reemplaza la anterior)

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: El paquete turístico actualizado

**⚠️ Nota**: Si se proporciona `serviciosIds`, reemplaza completamente la lista anterior de servicios.

---

### 3️⃣ Eliminar paquete turístico
```graphql
mutation {
  deletePaqueteTuristico(id: "673cd79d2e9af5567b9c0c20")
}
```
**Descripción**: Elimina un paquete turístico del sistema.

**Parámetros**:
- `id` (ID!): ID del paquete turístico a eliminar

**Permisos**: Requiere autenticación (Admin)

**Retorna**: `true` si la operación fue exitosa

---

### 4️⃣ Agregar servicio a un paquete
```graphql
mutation {
  addServicioToPaquete(
    paqueteId: "673cd79d2e9af5567b9c0c20"
    servicioId: "673cd79d2e9af5567b9c0c15"
  )
}
```
**Descripción**: Agrega un servicio adicional a un paquete turístico existente.

**Parámetros**:
- `paqueteId` (ID!): ID del paquete turístico
- `servicioId` (ID!): ID del servicio a agregar

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: `true` si la operación fue exitosa

**⚠️ Nota**: No se puede agregar un servicio que ya está en el paquete.

---

### 5️⃣ Eliminar servicio de un paquete
```graphql
mutation {
  removeServicioFromPaquete(
    paqueteId: "673cd79d2e9af5567b9c0c20"
    servicioId: "673cd79d2e9af5567b9c0c15"
  )
}
```
**Descripción**: Elimina un servicio de un paquete turístico existente.

**Parámetros**:
- `paqueteId` (ID!): ID del paquete turístico
- `servicioId` (ID!): ID del servicio a eliminar

**Permisos**: Requiere autenticación (Admin o Agente)

**Retorna**: `true` si la operación fue exitosa

---

## 📝 Notas Importantes

1. **Autenticación**: Todas las operaciones requieren un token JWT válido en el header:
   ```
   Authorization: Bearer <token>
   ```

2. **IDs**: Los IDs son ObjectIds de MongoDB en formato string de 24 caracteres hexadecimales.

3. **Composición de Paquetes**: 
   - Un paquete está compuesto por múltiples servicios
   - Puede incluir hoteles, vuelos, transporte, tours, comidas, etc.
   - El precio total puede ser menor a la suma de servicios individuales (descuento por paquete)

4. **Precio Total**: 
   - Es el precio de venta final del paquete completo
   - Generalmente ofrece descuento vs comprar servicios individuales
   - No se calcula automáticamente, debe especificarse manualmente

5. **Gestión de Servicios**:
   - Usar `serviciosIds` en create/update para definir lista completa
   - Usar `addServicioToPaquete` para agregar uno a la vez
   - Usar `removeServicioFromPaquete` para quitar uno a la vez

6. **Duración**: Representa los días totales del paquete turístico

---

## 🔐 Roles y Permisos

| Operación | Cliente | Agente | Admin |
|-----------|---------|--------|-------|
| getAllPaquetesTuristicos | ❌ | ✅ | ✅ |
| getPaqueteTuristicoById | ❌ | ✅ | ✅ |
| getPaquetesTuristicosByDestino | ❌ | ✅ | ✅ |
| searchPaquetesTuristicos | ❌ | ✅ | ✅ |
| createPaqueteTuristico | ❌ | ✅ | ✅ |
| updatePaqueteTuristico | ❌ | ✅ | ✅ |
| deletePaqueteTuristico | ❌ | ❌ | ✅ |
| addServicioToPaquete | ❌ | ✅ | ✅ |
| removeServicioFromPaquete | ❌ | ✅ | ✅ |

---

## 🧪 Ejemplos de Uso Completo

### Crear paquete completo Salar de Uyuni
```graphql
# Supongamos que ya tenemos estos servicios creados:
# - Servicio 1: Tour Salar 3 días (ID: 673cd79d2e9af5567b9c0c10)
# - Servicio 2: Hotel en Uyuni 2 noches (ID: 673cd79d2e9af5567b9c0c11)
# - Servicio 3: Transporte La Paz-Uyuni ida y vuelta (ID: 673cd79d2e9af5567b9c0c12)

mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Aventura en el Salar de Uyuni"
    descripcion: "Paquete completo de 3 días/2 noches incluyendo tour guiado al salar, hospedaje en hotel 4 estrellas, transporte desde La Paz y todas las comidas"
    destinoPrincipal: "Uyuni"
    duracionDias: 3
    precioTotalVenta: 2800.00
    serviciosIds: [
      "673cd79d2e9af5567b9c0c10",
      "673cd79d2e9af5567b9c0c11",
      "673cd79d2e9af5567b9c0c12"
    ]
  }) {
    id
    nombrePaquete
    destinoPrincipal
    duracionDias
    precioTotalVenta
    servicios {
      tipoServicio
      nombreServicio
      precioVenta
    }
  }
}
```

### Crear paquete Lago Titicaca
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Experiencia Místico Lago Titicaca"
    descripcion: "Tour de 2 días al Lago Titicaca, visitando Copacabana, Isla del Sol e Isla de la Luna. Incluye hospedaje, transporte y guía turístico"
    destinoPrincipal: "Copacabana"
    duracionDias: 2
    precioTotalVenta: 1500.00
    serviciosIds: [
      "SERVICIO_HOTEL_COPACABANA_ID",
      "SERVICIO_TOUR_TITICACA_ID",
      "SERVICIO_TRANSPORTE_ID"
    ]
  }) {
    id
    nombrePaquete
    precioTotalVenta
  }
}
```

### Buscar paquetes por destino
```graphql
query {
  getPaquetesTuristicosByDestino(destino: "Uyuni") {
    id
    nombrePaquete
    descripcion
    duracionDias
    precioTotalVenta
    servicios {
      id
      tipoServicio
      nombreServicio
    }
  }
}
```

### Agregar servicio adicional a paquete existente
```graphql
# Agregar servicio de cena especial al paquete
mutation {
  addServicioToPaquete(
    paqueteId: "673cd79d2e9af5567b9c0c20"
    servicioId: "SERVICIO_CENA_ESPECIAL_ID"
  )
}

# Consultar el paquete actualizado
query {
  getPaqueteTuristicoById(id: "673cd79d2e9af5567b9c0c20") {
    nombrePaquete
    servicios {
      tipoServicio
      nombreServicio
      precioVenta
    }
  }
}
```

### Actualizar paquete completo
```graphql
mutation {
  updatePaqueteTuristico(
    id: "673cd79d2e9af5567b9c0c20"
    input: {
      nombrePaquete: "Aventura Premium en el Salar de Uyuni"
      descripcion: "Paquete premium de 4 días/3 noches con hospedaje 5 estrellas, transporte privado, guía exclusivo y experiencias únicas"
      duracionDias: 4
      precioTotalVenta: 4500.00
      serviciosIds: [
        "673cd79d2e9af5567b9c0c10",
        "673cd79d2e9af5567b9c0c11",
        "673cd79d2e9af5567b9c0c12",
        "SERVICIO_HOTEL_PREMIUM_ID",
        "SERVICIO_CENA_ESPECIAL_ID"
      ]
    }
  ) {
    id
    nombrePaquete
    duracionDias
    precioTotalVenta
    servicios {
      nombreServicio
    }
  }
}
```

### Eliminar servicio de paquete
```graphql
mutation {
  removeServicioFromPaquete(
    paqueteId: "673cd79d2e9af5567b9c0c20"
    servicioId: "673cd79d2e9af5567b9c0c12"
  )
}
```

---

## 🌍 Ejemplos de Paquetes por Destino

### 🏔️ Paquete Salar de Uyuni
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Magia Blanca - Salar de Uyuni 3D/2N"
    descripcion: "Explora el espejo del cielo más grande del mundo. Incluye tour al salar, visita a Isla Incahuasi, cementerio de trenes, pueblo de Colchani, hospedaje y todas las comidas"
    destinoPrincipal: "Uyuni"
    duracionDias: 3
    precioTotalVenta: 2200.00
  }) { id }
}
```

### 🏛️ Paquete La Paz Cultural
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "La Paz Cultural y Aventura"
    descripcion: "City tour por La Paz, Valle de la Luna, teleférico, mercado de brujas, calle Jaén y museos. Incluye 2 noches de hotel, desayunos y transporte"
    destinoPrincipal: "La Paz"
    duracionDias: 2
    precioTotalVenta: 1200.00
  }) { id }
}
```

### 🌊 Paquete Lago Titicaca
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Lago Sagrado - Titicaca 2D/1N"
    descripcion: "Visita Copacabana, Isla del Sol, Isla de la Luna. Tour en lancha, guía bilingüe, hospedaje frente al lago, comidas típicas"
    destinoPrincipal: "Copacabana"
    duracionDias: 2
    precioTotalVenta: 1400.00
  }) { id }
}
```

### 🌳 Paquete Rurrenabaque - Amazonía
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Amazonía Boliviana - Aventura en la Selva"
    descripcion: "Tour de 4 días en la selva amazónica. Incluye vuelo, alojamiento en ecolodge, excursiones diarias, guía especializado y todas las comidas"
    destinoPrincipal: "Rurrenabaque"
    duracionDias: 4
    precioTotalVenta: 3500.00
  }) { id }
}
```

### 🏞️ Paquete Toro Toro
```graphql
mutation {
  createPaqueteTuristico(input: {
    nombrePaquete: "Parque Nacional Toro Toro - Huellas del Pasado"
    descripcion: "Explora cañones, cavernas, huellas de dinosaurios. Incluye transporte, guía, hospedaje 2 noches, comidas y entradas al parque"
    destinoPrincipal: "Toro Toro"
    duracionDias: 3
    precioTotalVenta: 1800.00
  }) { id }
}
```

---

## 💡 Tips para crear paquetes efectivos

1. **Nombre atractivo**: Use nombres descriptivos y evocadores
2. **Descripción detallada**: Especifique qué está incluido y qué no
3. **Precio competitivo**: Ofrezca descuento vs servicios individuales
4. **Servicios coherentes**: Agrupe servicios que tengan sentido juntos
5. **Duración realista**: La duración debe corresponder con los servicios incluidos
6. **Actualización regular**: Mantenga precios y disponibilidad actualizados
