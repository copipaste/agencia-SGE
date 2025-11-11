package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmarVentaRequest {
    private String metodoPago; // Opcional: "TARJETA" (por defecto si no se envía)
}

