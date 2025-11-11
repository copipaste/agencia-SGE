package com.agencia.agencia_backend.dto.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVentaRequest {

    @NotBlank(message = "El ID del paquete es obligatorio")
    private String paqueteId;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private String fechaInicio; // Formato: "2025-11-15"

    @NotBlank(message = "El modo es obligatorio")
    private String modo; // "RESERVA" o "COMPRA"
}

