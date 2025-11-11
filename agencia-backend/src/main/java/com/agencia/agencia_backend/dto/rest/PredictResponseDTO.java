package com.agencia.agencia_backend.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictResponseDTO {
    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("venta_id")
    private String ventaId;

    @JsonProperty("cliente_id")
    private String clienteId;

    @JsonProperty("probabilidad_cancelacion")
    private Double probabilidadCancelacion;

    @JsonProperty("recomendacion")
    private String recomendacion; // "sin_accion", "revisar_manual", "enviar_recordatorio"

    @JsonProperty("factores_riesgo")
    private List<String> factoresRiesgo;
}

