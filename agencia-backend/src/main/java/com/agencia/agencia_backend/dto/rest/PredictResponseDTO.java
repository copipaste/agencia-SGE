package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictResponseDTO {
    private Boolean success;
    private String ventaId;
    private String clienteId;
    private Double probabilidadCancelacion;
    private String recomendacion; // "sin_accion", "revisar_manual", "enviar_recordatorio"
    private List<String> factoresRiesgo;
}

