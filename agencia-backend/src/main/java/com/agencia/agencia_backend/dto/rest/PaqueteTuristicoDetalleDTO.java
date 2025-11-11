package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteTuristicoDetalleDTO {
    private String id;
    private String nombrePaquete;
    private String descripcion;
    private String destinoPrincipal;
    private Integer duracionDias;
    private Double precioTotalVenta;
    private List<ServicioDTO> servicios; // Lista completa de servicios con todos sus datos
}

