package com.agencia.agencia_backend.dto.bi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO genérico para respuestas de KPIs del servicio BI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiResponse {
    
    private String nombre;
    private Double valor;
    
    @JsonProperty("periodo_inicio")
    private String periodoInicio;
    
    @JsonProperty("periodo_fin")
    private String periodoFin;
    
    private String unidad;
    private String descripcion;
}
